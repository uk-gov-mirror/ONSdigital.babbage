package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.highcharts.HighChartsExportClient;
import com.github.onsdigital.highcharts.HighchartsChart;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartImageHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "linechart";
    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        System.out.println("Generating linechart image for " + requestedUri);
        HighchartsChart chartConfig = new LineChartConfigHandler().getChartConfig(requestedUri);
        InputStream stream = new HighChartsExportClient().getImage(chartConfig);
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
