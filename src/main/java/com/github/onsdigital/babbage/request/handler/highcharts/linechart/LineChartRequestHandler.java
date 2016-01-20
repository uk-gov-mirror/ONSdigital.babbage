package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Created by bren on 15/08/15.
 */
public class LineChartRequestHandler implements RequestHandler {
    public static final String REQUEST_TYPE = "linechart";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri);
        String html = TemplateService.getInstance().renderTemplate("highcharts/linechart", contentResponse.getDataStream());
        return new BabbageContentBasedStringResponse(contentResponse, html, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
