package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartImageRequestHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "linechartimage";
    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        System.out.println("Generating linechart image for " + requestedUri);
        String chartConfig = new LineChartConfigRequestHandler().getChartConfig(requestedUri);
        try (InputStream stream = HighChartsExportClient.getInstance().getImage(chartConfig, null)) {
            return new BabbageBinaryResponse(stream, CONTENT_TYPE);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
