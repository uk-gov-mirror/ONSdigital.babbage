package com.github.onsdigital.babbage.request.handler.highcharts.sparkline;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;

/**
 * Created by bren on 18/06/15.
 */
public class SparklineConfigRequestHandler implements RequestHandler {
    public static final String REQUEST_TYPE = "sparklineconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri, filter(ContentFilter.SERIES));
        String config = TemplateService.getInstance().renderTemplate("highcharts/config/sparklineconfig", contentResponse.getDataStream());
        return new BabbageContentBasedStringResponse(contentResponse, config);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
