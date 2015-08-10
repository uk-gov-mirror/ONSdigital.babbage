package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.chart.Chart;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.highcharts.HighchartsMarkdownChart;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MarkdownChartConfigHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "markdownchartconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        HighchartsMarkdownChart config = getChartConfig(requestedUri, zebedeeRequest);
        return new BabbageStringResponse(ContentUtil.serialise(config));
    }

    public HighchartsMarkdownChart getChartConfig(String requestedUri, ZebedeeRequest zebedeeRequest) throws ContentNotFoundException, IOException {
        DataService dataService = DataService.getInstance();
        Page page = ContentUtil.deserialisePage(dataService.readData(requestedUri + ".json", false, zebedeeRequest));

        if (!(page instanceof Chart)) {
            throw new IllegalArgumentException("Requested data is not a chart");
        }

        return new HighchartsMarkdownChart((Chart)page);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}

