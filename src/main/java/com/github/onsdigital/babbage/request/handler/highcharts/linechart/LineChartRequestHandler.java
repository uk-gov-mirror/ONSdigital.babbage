package com.github.onsdigital.babbage.request.handler.highcharts.linechart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by bren on 15/08/15.
 */
public class LineChartRequestHandler implements RequestHandler {
    public static final String REQUEST_TYPE = "linechart";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        try (ContentStream series = ContentClient.getInstance().getContentStream(requestedUri)) {
            String html = TemplateService.getInstance().renderTemplate("highcharts/linechart", series.getDataStream());
            return new BabbageStringResponse(html, MediaType.TEXT_HTML);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
