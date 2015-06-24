package com.github.onsdigital.highcharts;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;

import java.io.IOException;

/**
 * Created by bren on 23/06/15.
 */
public class LineChartTest {

    //TODO:Proper test
    public void testLineChart() throws IOException {
        LineChart chart = new LineChart(getTimeseries());
        System.out.println(chart);
    }

    private TimeSeries getTimeseries() throws IOException {
        TimeSeries timeSeries = (TimeSeries) ContentUtil.deserialisePage(DataService.getInstance().getDataStream("/economy/grossdomesticproductgdp/timeseries/ihyq"));
        return timeSeries;

    }
}
