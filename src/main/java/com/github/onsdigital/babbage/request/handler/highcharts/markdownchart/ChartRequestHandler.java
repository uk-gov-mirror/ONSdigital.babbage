package com.github.onsdigital.babbage.request.handler.highcharts.markdownchart;

import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Handles requests at the endpoint /chart.
 * Renders a chart and associated content in an isolated page.
 */
public class ChartRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "chart";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        try (ContentStream chartData = new MarkdownChartConfigHandler().getChartData(requestedUri)) {
            String html = TemplateService.getInstance().renderTemplate("highcharts/chart", chartData.getDataStream());
            return new BabbageStringResponse(html, MediaType.TEXT_HTML);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
