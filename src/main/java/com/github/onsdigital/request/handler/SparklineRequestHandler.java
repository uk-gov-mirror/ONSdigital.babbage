package com.github.onsdigital.request.handler;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.highcharts.HighChartsExportClient;
import com.github.onsdigital.highcharts.SparkLine;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by bren on 17/06/15.
 */
public class SparklineRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "sparkline";

    public static final String CONTENT_TYPE = "image/png";


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        String uriPath = StringUtils.removeStart(requestedUri, "/");
        System.out.println("Generating sparkline for " + uriPath);
        Page page = ContentUtil.deserialisePage(DataService.getInstance().getDataStream(requestedUri));
        if (!(page instanceof TimeSeries)) {
            throw new IllegalArgumentException("Requested data is not a timseries");
        }

        InputStream stream = new HighChartsExportClient().getImage(new SparkLine((TimeSeries) page));
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
