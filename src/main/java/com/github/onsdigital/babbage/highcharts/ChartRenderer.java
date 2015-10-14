package com.github.onsdigital.babbage.highcharts;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by bren on 09/10/15.
 */
public class ChartRenderer {
    private static ChartRenderer instance = new ChartRenderer();
    public static final int DEFAULT_CHART_WIDTH = 600;
    public static final int MAX_CHART_WIDTH = 1600;

    public static ChartRenderer getInstance() {
        return instance;
    }

    private ChartRenderer() {
    }

    /**
     * Fetches configuration from Zebedee Reader and converts it into chart configuration and renders using Handlebars templates for the chart type
     *
     * @param uri
     * @return
     * @throws IOException
     * @throws ContentReadException
     */
    public String getChartConfig(String uri) throws IOException, ContentReadException {
        return getChartConfig(uri, null);
    }

    /**
     * Fetches configuration from Zebedee Reader and converts it into chart configuration and renders using Handlebars templates for the chart type.
     * <p/>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     *
     * @param uri
     * @param width width of the chart, max=1500, min=300, default=600
     * @return
     * @throws IOException
     * @throws ContentReadException
     */
    public String getChartConfig(String uri, Integer width) throws IOException, ContentReadException {
        try (ContentStream stream = ContentClient.getInstance().getContentStream(uri)) {
            LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
            if (width == null) {
                width = DEFAULT_CHART_WIDTH;
            }
            additionalData.put("width", width);
            String config = TemplateService.getInstance().renderChartConfiguration(stream.getDataStream(), additionalData);
            return config;
        }
    }


    /**
     * Converts given data into chart configuration and renders using Handlebars templates for the chart type.
     * <p/>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     *
     * @param data
     * @param width width of the chart, max=1500, min=300, default=600
     * @return
     * @throws IOException
     * @throws ContentReadException
     */
    public String getChartConfigFor(String data, Integer width) throws IOException {
        LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
        if (width == null) {
            width = DEFAULT_CHART_WIDTH;
        }
        additionalData.put("width", width);
        String config = TemplateService.getInstance().renderChartConfiguration(data, additionalData);
        return config;
    }

    /**
     * Fetches configuration from Zebedee Reader and renders self contained chart html
     * <p/>
     * Optionally takes a width parameter, width is 600 by default, if width exceeds max, max width will be applied, if it is smaller than min, min width will apply
     *
     * @param uri
     * @param width width of the chart, max=1500, min=300, default=600
     * @return
     * @throws IOException
     * @throws ContentReadException
     */
    public String renderChart(String uri, Integer width) throws IOException, ContentReadException {
        try (ContentStream stream = ContentClient.getInstance().getContentStream(uri)) {
            LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
            additionalData.put("width", width);
            String html = TemplateService.getInstance().renderTemplate("highcharts/chart", stream.getDataStream(), additionalData);
            return html;
        }
    }

}
