package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;

/**
 * Created by bren on 17/06/15.
 */
public class SparklineImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "sparklineimage";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        System.out.println("Generating sparkline image for " + requestedUri);
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri, filter(ContentFilter.SERIES));
        String config = TemplateService.getInstance().renderTemplate("highcharts/config/sparklineconfig", contentResponse.getDataStream());
        try (InputStream stream = HighChartsExportClient.getInstance().getImage(config, null)) {
            return new BabbageContentBasedBinaryResponse(contentResponse, stream, CONTENT_TYPE);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
