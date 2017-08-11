package com.github.onsdigital.babbage.highcharts;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.logging.Log;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.TITLE_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.URI_PARAM;
import static com.github.onsdigital.babbage.highcharts.ChartConfigBuilder.WIDTH_PARAM;
import static java.text.MessageFormat.format;

/**
 * Created by bren on 09/10/15.
 */
public class ChartRenderer {

    static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    static final String CONTENT_DISPOSITION_HEADER_FMT = "attachment; filename=\"{0}.png\"";
    static final String DEFAULT_CONTENT_DISPOSITION_HEADER_FMT = "attachment; filename=\"Chart-{0}.png\"";
    static final String HIGHCHARTS_TEMPLATE = "highcharts/chart";
    static final String EMBEDED_HIGHCHARTS_TEMPLATE = "partials/highcharts/embeddedchart";
    static final String PNG_MIME_TYPE = "image/png";
    static final String DATA_PARAM = "data";
    static final String DEFAULT_TITLE_VALUE = "[Title]";
    public static final int DEFAULT_CHART_WIDTH = 700;
    public static final int MAX_CHART_WIDTH = 1600;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static ChartRenderer instance = new ChartRenderer();
    private ContentClient contentClient = ContentClient.getInstance();
    private TemplateService templateService = TemplateService.getInstance();
    private HighChartsExportClient highChartsExportClient = HighChartsExportClient.getInstance();

    public static ChartRenderer getInstance() {
        return instance;
    }

    // Singleton use getInstance method instead.
    private ChartRenderer() {
    }

    /**
     * Fetches configuration from Zebedee Reader and converts it into chart configuration and renders using Handlebars templates for the chart type.
     * <p>
     * Optionally takes a width parameter, width is 700 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChartConfig(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();

            try (InputStream inputStream = contentResponse.getDataStream()) {
                String chartConfig = templateService.renderChartConfiguration(inputStream, additionalData);
                new BabbageContentBasedStringResponse(contentResponse, chartConfig).apply(request, response);
            }
        }
    }

    /**
     * Fetches configuration from Zebedee Reader and renders self contained chart html
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderEmbeddedChart(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionData = new ChartConfigBuilder()
                    .width(getWidth(request))
                    .showTitle(request)
                    .showSubTitle(request)
                    .showSource(request)
                    .showNotes(request)
                    .getMap();

            try (InputStream inputStream = contentResponse.getDataStream()) {
                String renderedTemplate = templateService.renderTemplate(EMBEDED_HIGHCHARTS_TEMPLATE, inputStream,
                        additionData);
                new BabbageContentBasedStringResponse(contentResponse, renderedTemplate, MediaType.TEXT_HTML)
                        .applyEmbedded(request, response);
            }
        }
    }

    public void renderChartImage(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Integer width = getWidth(request);
            Map<String, Object> additionalData = new ChartConfigBuilder().width(width).getMap();

            String chartConfig;
            try (InputStream in = contentResponse.getDataStream()) {
                chartConfig = templateService.renderChartConfiguration(in, additionalData);

                try (
                        InputStream imageInputStream = highChartsExportClient.getImage(chartConfig, width);
                        InputStream contentResponseInputStream = contentResponse.getDataStream()
                ) {
                    BabbageResponse babbabeResp = new BabbageContentBasedBinaryResponse(contentResponse, imageInputStream, PNG_MIME_TYPE);
                    babbabeResp.addHeader(CONTENT_DISPOSITION_HEADER, getImageContentDispositionHeader(uri, contentResponseInputStream));
                    babbabeResp.apply(request, response);
                }
            }
        }
    }

    private String getImageContentDispositionHeader(String uri, InputStream chartConfig) throws IOException {
        Map<String, Object> map = mapper.readValue(chartConfig, Map.class);
        String chartTitle = (String) map.get(TITLE_PARAM);
        if (StringUtils.isEmpty(chartTitle) || StringUtils.equalsIgnoreCase(chartTitle, DEFAULT_TITLE_VALUE)) {
            return format(DEFAULT_CONTENT_DISPOSITION_HEADER_FMT, Paths.get(uri).getFileName().toString());
        }
        return format(CONTENT_DISPOSITION_HEADER_FMT, chartTitle);
    }

    /**
     * Converts given data into chart configuration and renders using Handlebars templates for the chart type.
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChartConfigFor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter(DATA_PARAM);
        if (StringUtils.isEmpty(data)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            IOUtils.write("Please specify chart data to get chart configuration for", response.getOutputStream());
        }
        Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();
        String renderedTemplate = templateService.renderChartConfiguration(data, additionalData);
        new BabbageStringResponse(renderedTemplate).apply(request, response);
    }

    /**
     * Fetches configuration from Zebedee Reader and renders self contained chart html
     * <p>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     */
    public void renderChart(HttpServletRequest request, HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter(URI_PARAM);
        if (assertUri(uri, request, response)) {
            ContentResponse contentResponse = contentClient.getContent(uri);
            Map<String, Object> additionalData = new ChartConfigBuilder().width(getWidth(request)).getMap();
            try (InputStream inputStream = contentResponse.getDataStream()) {
                String renderedTemplate = templateService.renderTemplate(HIGHCHARTS_TEMPLATE, inputStream, additionalData);
                new BabbageContentBasedStringResponse(contentResponse, renderedTemplate, MediaType.TEXT_HTML)
                        .apply(request, response);
            }
        }
    }

    private boolean assertUri(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            IOUtils.write("Please specify uri of the chart", response.getOutputStream());
            return false;
        }
        return true;
    }

    public static Integer getWidth(HttpServletRequest request) {
        try {
            String width = request.getParameter(WIDTH_PARAM);
            if (StringUtils.isNotEmpty(width)) {
                return calculateWidth(Integer.parseInt(width));
            }
        } catch (NumberFormatException e) {
            Log.build("Chart width not a valid number, default width will be used.", Level.DEBUG).log();
        }
        return DEFAULT_CHART_WIDTH;
    }

    private static int calculateWidth(Integer width) {
        int w = width == null ? DEFAULT_CHART_WIDTH : width;
        if (w < 0) {
            w = DEFAULT_CHART_WIDTH;
        } else if (w > MAX_CHART_WIDTH) {
            w = MAX_CHART_WIDTH;
        }
        return w;
    }
}
