package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 17/06/15.
 */
public class SparklineImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "sparklineimage";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        System.out.println("Generating sparkline image for " + requestedUri);
        String config = new SparklineConfigRequestHandler().getChartConfig(requestedUri);
        InputStream stream = HighChartsExportClient.getInstance().getImage(config, null);
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
