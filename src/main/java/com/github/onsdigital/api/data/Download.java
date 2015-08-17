package com.github.onsdigital.api.data;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.bean.DateVal;
import com.github.onsdigital.bean.DownloadRequest;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.LocalFileDataService;
import com.github.onsdigital.util.CSVGenerator;
import com.github.onsdigital.util.XLSXGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Serves data files in xls or csv format
 */

@Api
public class Download {

    private static final String XLSX = "xlsx";

    /*

    We can not pass DownloadRequest as a parameter to this method as we did in the alpha as shown below. If we do Restolino tries deserialising it automatically.
    But since download buttons need to work in no-js environments, we use pure html form to make post request. enctype application/json standard is still a draft and not supported in old browsers
    ( maybe not yet in new ones ? ).That's why reading request parameters manually

    old method : public void post(@Context HttpServletRequest request, @Context HttpServletResponse response, DownloadRequest downloadRequest ) throws IOException {
     */

    @POST
    public void post(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        try {
            DownloadRequest downloadRequest = initializeDownloadRequest(request);
            System.out.println("Download request recieved" + downloadRequest);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + (downloadRequest.fileName != null ? downloadRequest.fileName : "data") + "." + downloadRequest.type + "\"");
            response.setCharacterEncoding("UTF8");
            response.setContentType("application/" + downloadRequest.type);
            processRequest(response.getOutputStream(), downloadRequest);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.setContentType("text/plain");
            response.getWriter().write("An error occured while processing this download request");
        }
    }

    private DownloadRequest initializeDownloadRequest(@Context HttpServletRequest request) {
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.type = request.getParameter("type");
        if (org.apache.commons.lang3.StringUtils.isBlank(downloadRequest.type)) {
            //Send in xlsx format by default
            downloadRequest.type = XLSX;
        }
        downloadRequest.cdidList = getParameterList(request, "cdid");
        downloadRequest.uriList = getParameterList(request, "uri");
        downloadRequest.fileName = request.getParameter("fileName");

        /*From*/
        String fromYear = request.getParameter("fromYear");
        if (fromYear != null) {
            downloadRequest.from = new DateVal();
            downloadRequest.from.month = request.getParameter("fromMonth");
            downloadRequest.from.quarter = request.getParameter("fromQuarter");
            downloadRequest.from.year = Integer.parseInt(fromYear);
        }

        /*To*/
        String toYear = request.getParameter("toYear");
        if (toYear != null) {
            downloadRequest.to = new DateVal();
            downloadRequest.to.month = request.getParameter("toMonth");
            downloadRequest.to.quarter = request.getParameter("toQuarter");
            downloadRequest.to.year = Integer.parseInt(toYear);
        }

        return downloadRequest;
    }

    private List<String> getParameterList(HttpServletRequest request, String parameterName) {
        String[] params = request.getParameterValues(parameterName);
        if (params != null) {
            return Arrays.asList(params);
        }
        return null;
    }

    private void processRequest(OutputStream output, DownloadRequest downloadRequest) throws IOException {

        Path tempDirectory = FileSystems.getDefault().getPath(FileUtils.getTempDirectoryPath());
        String from = downloadRequest.from == null ? "" : downloadRequest.from.toString();
        String to = downloadRequest.to == null ? "" : downloadRequest.to.toString();
        final String fileName = downloadRequest.fileName + "_" + from + "-" + to + "." + downloadRequest.type;

        //If file exists on temp api read it from temp
        Path tempFile = tempDirectory.resolve(fileName);
        if (Files.exists(tempFile)) {
            FileTime lastModifiedTime = Files.getLastModifiedTime(tempFile);
            if(new Date().getTime() -  (lastModifiedTime.toMillis()) < TimeUnit.MINUTES.toMillis(Configuration.GENERAL.getGlobalCacheTimeout())) {
                System.out.println("Find generated file in temp directory:" + fileName);
                IOUtils.copy(Files.newInputStream(tempFile), output);
                return;
            } else {
                System.out.println("Deleting expired file");
                Files.delete(tempFile);
            }
        }

        System.out.println("File not generated before, generating:" + fileName);
        OutputStream outputStream = Files.newOutputStream(tempFile);

        // Normally only uriList or cdidList should be present in the request,
        // but let's be lenient in what we'll accept:
        List<TimeSeries> TimeSeries = new ArrayList<TimeSeries>();

        // Process URIs
        if (downloadRequest.uriList != null) {
            for (String uri : downloadRequest.uriList) {
                try (InputStream input = LocalFileDataService.getInstance().getDataStream(uri)) {
                    TimeSeries.add(ContentUtil.deserialise(input, TimeSeries.class));
                }
            }
        }

        // Process CDIDs
        if (downloadRequest.cdidList != null) {
            Map<String, TimeSeries> TimeSeriesMap = Cdid.getTimeSeries(downloadRequest.cdidList);
            for (TimeSeries TimeSeries2 : TimeSeriesMap.values()) {
                TimeSeries.add(TimeSeries2);
            }
        }

        // Collate into a "grid":
        Map<String, TimeseriesValue[]> data = collateData(TimeSeries, downloadRequest);

        // Apply the range:
        data = applyRange(data, toDate(downloadRequest.from), toDate(downloadRequest.to));

        switch (downloadRequest.type) {
            case "xlsx":
                new XLSXGenerator(TimeSeries, data).write(outputStream);
                break;
            case "csv":
                new CSVGenerator(TimeSeries, data).write(outputStream);
                break;
            default:
                break;
        }
        IOUtils.closeQuietly(outputStream);

        IOUtils.copy(Files.newInputStream(tempFile), output);

    }

    private Date toDate(DateVal from) {
        Date result = null;
        if (from != null) {
            result = TimeseriesValue.toDate(from.toString());
        }
        return result;
    }

    /**
     * Collates data from the given TimeSeries into an ordered map. This
     * provides a "data grid" suitable for writing out in tabular format.
     *
     * @param TimeSeriesList
     * @return
     */
    private Map<String, TimeseriesValue[]> collateData(List<TimeSeries> TimeSeriesList, DownloadRequest downloadRequest) {

        // We want an ordered map of date strings -> values as the result:
        Map<String, TimeseriesValue[]> result = new LinkedHashMap<>();

        boolean year = true;
        boolean quarter = true;
        boolean month = true;

        if (downloadRequest.from != null) {
            if (StringUtils.isNotBlank(downloadRequest.from.quarter)) {
                year = false;
                month = false;
            } else if (StringUtils.isNotBlank(downloadRequest.from.month)) {
                year = false;
                quarter = false;
            } else if (downloadRequest.from.year > 0) {
                quarter = false;
                month = false;
            }
        }

        if (year) {
            // Collate years:
            Map<Date, TimeseriesValue[]> years = new TreeMap<>();
            for (int l = 0; l < TimeSeriesList.size(); l++) {
                for (TimeseriesValue value : TimeSeriesList.get(l).years) {
                    addValue(value, l, TimeSeriesList, years);
                }
            }
            addToResult(years, result);
        }

        if (quarter) {
            // Collate quarters:
            Map<Date, TimeseriesValue[]> quarters = new TreeMap<>();
            for (int l = 0; l < TimeSeriesList.size(); l++) {
                for (TimeseriesValue value : TimeSeriesList.get(l).quarters) {
                    addValue(value, l, TimeSeriesList, quarters);
                }
            }
            addToResult(quarters, result);
        }

        if (month) {
            // Collate years:
            Map<Date, TimeseriesValue[]> months = new TreeMap<>();
            for (int l = 0; l < TimeSeriesList.size(); l++) {
                for (TimeseriesValue value : TimeSeriesList.get(l).months) {
                    addValue(value, l, TimeSeriesList, months);
                }
            }
            addToResult(months, result);
        }

        return result;
    }

    private Map<String, TimeseriesValue[]> applyRange(Map<String, TimeseriesValue[]> data, Date from, Date to) {

        // We want an ordered map of date strings -> values as the result:
        Map<String, TimeseriesValue[]> result = new LinkedHashMap<>();

        boolean add = false;
        for (String key : data.keySet()) {
            Date date = TimeseriesValue.toDate(key);
            // Start adding if no from date has been specified:
            if ((!add && from == null) || date.equals(from)) {
                System.out.println("Starting range at " + key);
                add = true;
            }
            if (add) {
                // System.out.print(".");
                result.put(key, data.get(key));
            }
            if (date.equals(to)) {
                System.out.println("Ending range at " + key);
                break;
            }
        }

        return result;
    }

    /**
     * Adds a single TimeSeries value to a data block (yearly, quarterly or
     * monthly).
     *
     * @param value          The value to be added.
     * @param listIndex      The index of the current TimeSeries within the collection of
     *                       TimeSeries to be downloaded.
     * @param TimeSeriesList The collection of TimeSeries to be downloaded - used to get a
     *                       length for the array.
     * @param data           The map into which the value will be added.
     */
    private void addValue(TimeseriesValue value, int listIndex, List<TimeSeries> TimeSeriesList, Map<Date, TimeseriesValue[]> data) {

        Date key = value.toDate();

        // Ensure we have a "row" for this date:
        if (!data.containsKey(key)) {
            data.put(key, new TimeseriesValue[TimeSeriesList.size()]);
        }

        // Put the value into the "grid":
        data.get(key)[listIndex] = value;
    }

    /**
     * Adds a block of data to the overall result.
     *
     * @param valuesMap The block of data to be added.
     * @param result    The overall map that the data wil be added to.
     */
    private void addToResult(Map<Date, TimeseriesValue[]> valuesMap, Map<String, TimeseriesValue[]> result) {

        for (TimeseriesValue[] values : valuesMap.values()) {

            // Select a value to use as the overall row date:
            String date = null;
            key:
            for (TimeseriesValue value : values) {
                if (value != null) {
                    date = value.date;
                    break key;
                }
            }

            // Add this row to the result:
            result.put(date, values);
        }
    }

}
