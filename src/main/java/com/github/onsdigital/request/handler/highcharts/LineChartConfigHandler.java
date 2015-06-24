package com.github.onsdigital.request.handler.highcharts;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.highcharts.BaseChart;
import com.github.onsdigital.highcharts.LineChart;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 18/06/15.
 */
public class LineChartConfigHandler implements RequestHandler {

    public static final String REQUEST_TYPE = "linechartconfig";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return get(requestedUri, request, null);
    }


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request, ZebedeeRequest zebedeeRequest) throws Exception {
        System.out.println("Generating linechart config for " + requestedUri);
        return new BabbageStringResponse(getChartConfig(requestedUri).toString());
    }

    BaseChart getChartConfig(String requestedUri) throws IOException {
        Page page = ContentUtil.deserialisePage(DataService.getInstance().getDataStream(requestedUri));
        if (!(page instanceof TimeSeries)) {
            throw new IllegalArgumentException("Requested data is not a timseries");
        }
        return new LineChart((TimeSeries) page);

    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
