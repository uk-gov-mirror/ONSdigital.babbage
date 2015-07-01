package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.highcharts.BaseChart;
import com.github.onsdigital.highcharts.HighChartsExportClient;
import com.github.onsdigital.request.handler.base.RequestHandler;
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
        return get(requestedUri, request, null);
    }


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        System.out.println("Generating linechart image for " + requestedUri);
        BaseChart chartConfig = new LineChartConfigHandler().getChartConfig(requestedUri);
        InputStream stream = new HighChartsExportClient().getImage(chartConfig);
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
