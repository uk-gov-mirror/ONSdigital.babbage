package com.github.onsdigital.babbage.request.handler.highcharts.markdownchart;

import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Created by bren on 01/07/15.
 */
public class MarkdownChartImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "chartimage";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        System.out.println("Generating search chart image for " + requestedUri);
        String config = new MarkdownChartConfigHandler().getChartConfig(requestedUri);
        InputStream stream = HighChartsExportClient.getInstance().getImage(config);
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
