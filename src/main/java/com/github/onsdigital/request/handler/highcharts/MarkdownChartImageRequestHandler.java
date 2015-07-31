package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.highcharts.HighChartsExportClient;
import com.github.onsdigital.highcharts.HighchartsMarkdownChart;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import com.github.onsdigital.request.response.BabbageResponse;

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
        return get(requestedUri, request, null);
    }

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        System.out.println("Generating search chart image for " + requestedUri);
        HighchartsMarkdownChart config = new MarkdownChartConfigHandler().getChartConfig(requestedUri, zebedeeRequest);
        InputStream stream = new HighChartsExportClient().getImage(ContentUtil.serialise(config));
        return new BabbageBinaryResponse(stream, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
