package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;

/**
 * Created by bren on 18/06/15.
 */
public class SparklineRequestHandler implements RequestHandler {
    public static final String REQUEST_TYPE = "sparkline";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        try (
                ContentStream series = ContentClient.getInstance().getContentStream(requestedUri, filter(ContentFilter.SERIES))
        ) {
            String html = TemplateService.getInstance().renderTemplate("highcharts/sparkline", series.getDataStream());
            return new BabbageStringResponse(html, MediaType.TEXT_HTML);

        }

    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
