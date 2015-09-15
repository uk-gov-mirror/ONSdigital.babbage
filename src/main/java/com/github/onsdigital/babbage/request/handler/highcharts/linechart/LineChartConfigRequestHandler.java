package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartConfigRequestHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "linechartconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageStringResponse(getChartConfig(requestedUri).toString());
    }

    String getChartConfig(String requestedUri) throws IOException, ContentReadException {

        try (ContentStream series = ContentClient.getInstance().getContentStream(requestedUri, ContentClient.filter(ContentFilter.SERIES));
             ContentStream description = ContentClient.getInstance().getContentStream(requestedUri, ContentClient.filter(ContentFilter.DESCRIPTION))
        ) {
            String config = TemplateService.getInstance().renderTemplate("highcharts/config/linechartconfig", series.getDataStream(), JsonUtil.toMap(description.getDataStream()));
            return config;
        }

    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
