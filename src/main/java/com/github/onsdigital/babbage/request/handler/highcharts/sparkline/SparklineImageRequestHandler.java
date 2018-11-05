package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;
import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

/**
 * Created by bren on 17/06/15.
 */
public class SparklineImageRequestHandler extends BaseRequestHandler {

    private static final String REQUEST_TYPE = "sparklineimage";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        logEvent().uri(requestedUri).info("generating sparkline image for uri");

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
