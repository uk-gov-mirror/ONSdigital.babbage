package com.github.onsdigital.highcharts;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.googlecode.wickedcharts.highcharts.jackson.JsonRenderer;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by bren on 18/06/15.
 */
public abstract class BaseChart {
    private Set<TimeseriesValue> data;
    private TimeSeries timeSeries;
    private Options options;

    public BaseChart(SeriesType seriesType, TimeSeries timeSeries) {

        this.timeSeries = timeSeries;
        data = resolveData(timeSeries);
        options = new Options();
        ChartOptions chartOptions = new ChartOptions(seriesType);
        chartOptions.setBackgroundColor(new NullColor());
        options.setChart(chartOptions);
        options.setExporting(new ExportingOptions().setUrl(Configuration.getHighchartsExportSeverUrl()));

        options.setCredits(new CreditOptions().setEnabled(false));
        options.setSeries(prepareData(data));

        configureChart(options);
    }

    private List<Series<?>> prepareData(Set<TimeseriesValue> data) {
        List<Series<?>> seriesList = new ArrayList<>();
        PointSeries series = new PointSeries();

        for (Iterator<TimeseriesValue> iterator = data.iterator(); iterator.hasNext(); ) {
            TimeseriesValue timeseries = iterator.next();
            series.addPoint(new Point().setName(timeseries.date).setY(Double.valueOf(timeseries.value)));
        }
        seriesList.add(series);
        return seriesList;
    }

    //Resolve what frequenct to be used. Will try years, quarters and months respectively and use first one available
    private Set<TimeseriesValue> resolveData(TimeSeries timeSeries) {
        if (timeSeries.years.size() > 0) {
            return timeSeries.years;
        } else if (timeSeries.quarters.size() > 0) {
            return timeSeries.quarters;
        } else if (timeSeries.months.size() > 0) {
            return timeSeries.months;
        }
        throw new IllegalArgumentException("Time series " + timeSeries.getDescription().getTitle() + " does not contain any data ");
    }

    protected Integer resolveTickInterval(Set<TimeseriesValue> data) {
        int length = data.size();
        if (length <= 20) {
            return 1;
        } else if (length <= 80) {
            return 4;
        } else if (length <= 240) {
            return 12;
        } else if (length <= 480) {
            return 48;
        } else if (length <= 960) {
            return 96;
        } else {
            return 192;
        }
    }

    public String toJson() {
        return new JsonRenderer().toJson(this.options);
    }

    protected Set<TimeseriesValue> getData() {
        return data;
    }

    protected abstract void configureChart(Options chartOptions);

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }
}
