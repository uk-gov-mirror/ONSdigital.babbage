package com.github.onsdigital.highcharts;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

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
    private String config;
    private Double min;

    public BaseChart(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
        this.config = getChartConfig();
        data = resolveData(timeSeries);
        List<Value> values = prepareData(data);
        config = config.replace("':data:'", new GsonBuilder().serializeNulls().create().toJson(values));
        config = config.replace("':tickInterval:'", String.valueOf(resolveTickInterval(data)));
        config = config.replace("':yMin:'", String.valueOf(min));
    }

    private List<Value> prepareData(Set<TimeseriesValue> data) {
        List<Value> seriesList = new ArrayList<>();
        for (Iterator<TimeseriesValue> iterator = data.iterator(); iterator.hasNext(); ) {
            TimeseriesValue timeseries = iterator.next();
            Double value = StringUtils.isNotEmpty(timeseries.value) ?  Double.valueOf(timeseries.value) : null;
            seriesList.add(new Value().setName(timeseries.date).setY(value));

            if (value == null) {
                continue;
            }

            if (min == null) {
                min = value;
            } else {
                if (value < min) {
                    min = value;
                }
            }
        }

        if (min < 0) {
            min = min - 1;
        } else {
            min = 0d;
        }

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

    @Override
    public String toString() {
        return config;
    }

    protected Set<TimeseriesValue> getData() {
        return data;
    }

    protected abstract String getChartConfig();

    protected TimeSeries getTimeSeries() {
        return timeSeries;
    }

    protected String quote(String string) {
        return "'" + string + "'";
    }
}
