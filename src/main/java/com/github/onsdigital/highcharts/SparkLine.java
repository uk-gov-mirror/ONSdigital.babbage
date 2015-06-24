package com.github.onsdigital.highcharts;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;

/**
 * Created by bren on 17/06/15.
 */
public class SparkLine extends BaseChart {

    public SparkLine(TimeSeries timeSeries) {
        super(timeSeries);
    }

    @Override
    public String getChartConfig() {
        return Configuration.getSparklineConfig();
    }

}
