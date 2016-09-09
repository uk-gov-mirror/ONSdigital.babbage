package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;

/**
 * Created by bren on 18/06/15.
 */
public class SparklineRequestHandler extends BaseRequestHandler {
    public static final String REQUEST_TYPE = "sparkline";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri, filter(ContentFilter.SERIES));
        String html = TemplateService.getInstance().renderTemplate("highcharts/sparkline", contentResponse.getDataStream());
        return new BabbageContentBasedStringResponse(contentResponse, html, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
