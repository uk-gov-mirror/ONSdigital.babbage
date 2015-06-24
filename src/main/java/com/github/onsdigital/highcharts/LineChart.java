package com.github.onsdigital.highcharts;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;

/**
 * Created by bren on 18/06/15.
 */
public class LineChart extends BaseChart {

    public LineChart(TimeSeries timeSeries) {
        super(timeSeries);
    }

    @Override
    protected String getChartConfig() {
        String config = Configuration.getLinechartConfig();
        config = config.replace("timeseries.description.title", quote(getTimeSeries().getDescription().getTitle()));
        config = config.replace("timeseries.description.source", quote(getTimeSeries().getDescription().getSource()));
        config = config.replace("timeseries.description.cdid", quote(getTimeSeries().getDescription().getCdid()));
        config = config.replace("timeseries.description.unit", quote(getTimeSeries().getDescription().getUnit()));
        config = config.replace("timeseries.description.preUnit", quote(getTimeSeries().getDescription().getPreUnit()));
        return config;
    }
}
