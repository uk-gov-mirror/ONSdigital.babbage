//package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;
//
//import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
//import com.github.onsdigital.babbage.request.response.BabbageBinaryResponse;
//import com.github.onsdigital.babbage.request.response.BabbageResponse;
//import com.github.onsdigital.highcharts.HighChartsExportClient;
//import com.github.onsdigital.highcharts.HighchartsChart;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.InputStream;
//
///**
// * Created by bren on 17/06/15.
// */
//public class SparklineImageRequestHandler implements RequestHandler {
//
//    private static final String REQUEST_TYPE = "sparkline";
//
//    public static final String CONTENT_TYPE = "image/png";
//
//    @Override
//    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
//        System.out.println("Generating sparkline image for " + requestedUri);
//        HighchartsChart chartConfig = new SparklineConfigRequestHandler().getChartConfig(requestedUri);
//        InputStream stream = new HighChartsExportClient().getImage(chartConfig);
//        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//}
