package com.github.onsdigital.highcharts;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by bren on 23/06/15.
 */
public class SparklineTest {

    //TODO:Proper test
    public void testSparkline() throws IOException {
        SparkLine chart = new SparkLine(getTimeseries());
        System.out.println(chart);
    }

    private TimeSeries getTimeseries() throws IOException {
        TimeSeries timeSeries = (TimeSeries) ContentUtil.deserialisePage(DataService.getInstance().getDataStream("/economy/grossdomesticproductgdp/timeseries/ihyq"));
        return timeSeries;

    }
}
