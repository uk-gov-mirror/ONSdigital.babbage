package com.github.onsdigital.babbage.request.handler.highcharts.markdownchart;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.response.BabbageStringResponse;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.chart.Chart;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.highcharts.HighchartsMarkdownChart;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MarkdownChartConfigHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "markdownchartconfig";

    @Override
    public BabbageStringResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        String chart = getChartConfig(requestedUri);
        return new BabbageStringResponse(chart);
    }

    public String getChartConfig(String requestedUri) throws ContentReadException, IOException {
        ContentStream stream = getChartData(requestedUri);
        Page page = ContentUtil.deserialisePage(stream.getDataStream());
        if (!(page instanceof Chart)) {
            throw new IllegalArgumentException("Requested data is not a chart");
        }
        return JsonUtil.toJson(new HighchartsMarkdownChart((Chart) page));
    }

    public ContentStream getChartData(String uri) throws IOException, ContentReadException {
        return  ContentClient.getInstance().getResource(uri + ".json");
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}

