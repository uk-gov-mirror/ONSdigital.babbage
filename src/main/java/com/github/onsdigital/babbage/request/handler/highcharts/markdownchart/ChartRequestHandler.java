package com.github.onsdigital.babbage.request.handler.highcharts.markdownchart;

import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.response.BabbageResponse;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
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
        ContentStream chartData = new MarkdownChartConfigHandler().getChartData(requestedUri);
        String html = TemplateService.getInstance().renderTemplate("charts/chart", chartData.getDataStream());
        return new BabbageStringResponse(html, MediaType.TEXT_HTML);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
