package com.github.onsdigital.highcharts;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.github.onsdigital.content.util.ContentUtil;
import com.googlecode.wickedcharts.highcharts.jackson.JsonRenderer;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.ColorReference;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by bren on 17/06/15.
 */
public class SparkLine {

    private Set<TimeseriesValue> data;
    private Options chartOptions;


    public SparkLine(TimeSeries timeSeries) {
        data = resolveData(timeSeries);
        this.chartOptions = configureChart();

    }

    public Options configureChart() {
        Options options = new Options();
        ChartOptions chart = new ChartOptions().setType(SeriesType.AREA);
        options.setChart(chart);

        chart.setBackgroundColor(new NullColor());
        chart.setStyle(new CssStyle().setProperty("oveflow", "visible"));
        chart.setBorderWidth(0);
        chart.setMarginTop(0);
        chart.setMarginBottom(0);
        chart.setMarginLeft(0);
        chart.setMarginRight(0);
        options.setTitle(new Title(""));
        /*xAxis*/
        Axis xAxis = new Axis();
        xAxis.setTickInterval(Float.valueOf(resolveTickInterval(data)));
        //labels
        Labels labels = new Labels();
        labels.setFormatter(new Function().setFunction(
                "if (this.isFirst) " +
                        "{ " +
                        "return this.date " +
                        "} " +
                        " if (this.isLast) { " +
                        " return this.date " +
                        "}"));
        xAxis.setLabels(labels);
        xAxis.setTickLength(1);
        options.setxAxis(xAxis);

        /*yAxis*/
        Axis yAxis = new Axis();
        yAxis.setEndOnTick(false);
        yAxis.setStartOnTick(false);
        //labels
        Labels yLabels = new Labels();
        yLabels.setEnabled(false);
        yAxis.setLabels(yLabels);
        yAxis.setGridLineWidth(0);
        options.setyAxis(yAxis);


        /*legend*/
        options.setLegend(new Legend().setEnabled(false));
        options.setTooltip(new Tooltip().setEnabled(false));

        /*Exporting*/
        options.setExporting(new ExportingOptions().setEnabled(false));

        /*plot options*/
        //TODO: Border color silver
        PlotOptionsChoice plotOptionsChoice = new PlotOptionsChoice();
        options.setPlotOptions(plotOptionsChoice);
        //column
        plotOptionsChoice.setColumn(new PlotOptions().setBorderColor(Color.gray));
        //Series
        plotOptionsChoice.setSeries(new PlotOptions()
                .setAnimation(false)
                .setLineWidth(1)
                .setShadow(false)
                .setStates(new StatesChoice()
                        .setHover(new State()
                                .setLineWidth(1)))
                .setMarker(new Marker()
                        .setRadius(1))
                .setStates(new StatesChoice()
                        .setHover(new State()
                                .setRadius(2)))
                .setFillOpacity(0.25f)
                .setEnableMouseTracking(false));


        options.setCredits(new CreditOptions().setEnabled(false));

        /*Data*/
        options.setSeries(prepareData(data));

        return options;
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


    private Integer resolveTickInterval(Set<TimeseriesValue> data) {
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

    public String toJson() {
        return new JsonRenderer().toJson(this.chartOptions);
    }

    public static void main(String args[]) throws IOException {
        TimeSeries timeSeries = (TimeSeries) ContentUtil.deserialisePage("{\n" +
                "  \"years\": [\n" +
                "    {\n" +
                "      \"date\": \"1989\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991\",\n" +
                "      \"value\": \"7.5\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992\",\n" +
                "      \"value\": \"4.3\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008\",\n" +
                "      \"value\": \"3.6\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010\",\n" +
                "      \"value\": \"3.3\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011\",\n" +
                "      \"value\": \"4.5\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"quarters\": [\n" +
                "    {\n" +
                "      \"date\": \"1989 Q1\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 Q2\",\n" +
                "      \"value\": \"5.3\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 Q3\",\n" +
                "      \"value\": \"5.1\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 Q4\",\n" +
                "      \"value\": \"5.5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 Q1\",\n" +
                "      \"value\": \"5.9\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 Q2\",\n" +
                "      \"value\": \"6.7\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 Q3\",\n" +
                "      \"value\": \"7.6\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 Q4\",\n" +
                "      \"value\": \"7.9\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 Q1\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 Q2\",\n" +
                "      \"value\": \"8.4\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 Q3\",\n" +
                "      \"value\": \"7.7\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 Q4\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 Q1\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 Q2\",\n" +
                "      \"value\": \"4.3\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 Q3\",\n" +
                "      \"value\": \"3.3\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 Q4\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 Q1\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 Q2\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 Q3\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 Q4\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 Q1\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 Q2\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 Q3\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 Q4\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 Q1\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 Q2\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 Q3\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 Q4\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 Q1\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 Q2\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 Q3\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 Q4\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 Q1\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 Q2\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 Q3\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 Q4\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 Q1\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 Q2\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 Q3\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 Q4\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 Q1\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 Q2\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 Q3\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 Q4\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 Q1\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 Q2\",\n" +
                "      \"value\": \"0.6\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 Q3\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 Q4\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 Q1\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 Q2\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 Q3\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 Q4\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 Q1\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 Q2\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 Q3\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 Q4\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 Q1\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 Q2\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 Q3\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 Q4\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 Q1\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 Q2\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 Q3\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 Q4\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 Q1\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 Q2\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 Q3\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 Q4\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 Q1\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 Q2\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 Q3\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 Q4\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 Q1\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 Q2\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 Q3\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 Q4\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 Q1\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 Q2\",\n" +
                "      \"value\": \"3.4\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 Q3\",\n" +
                "      \"value\": \"4.8\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 Q4\",\n" +
                "      \"value\": \"3.9\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 Q1\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 Q2\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 Q3\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 Q4\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 Q1\",\n" +
                "      \"value\": \"3.3\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 Q2\",\n" +
                "      \"value\": \"3.5\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 Q3\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 Q4\",\n" +
                "      \"value\": \"3.4\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 Q1\",\n" +
                "      \"value\": \"4.1\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 Q2\",\n" +
                "      \"value\": \"4.4\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 Q3\",\n" +
                "      \"value\": \"4.7\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 Q4\",\n" +
                "      \"value\": \"4.6\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 Q1\",\n" +
                "      \"value\": \"3.5\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 Q2\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 Q3\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 Q4\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 Q1\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 Q2\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 Q3\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"quarter\": \"Q3\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 Q4\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"quarter\": \"Q4\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 Q1\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"quarter\": \"Q1\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 Q2\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"quarter\": \"Q2\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"months\": [\n" +
                "    {\n" +
                "      \"date\": \"1989 JAN\",\n" +
                "      \"value\": \"4.9\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 FEB\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 MAR\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 APR\",\n" +
                "      \"value\": \"5.3\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 MAY\",\n" +
                "      \"value\": \"5.3\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 JUN\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 JUL\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 AUG\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 SEP\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 OCT\",\n" +
                "      \"value\": \"5.5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 NOV\",\n" +
                "      \"value\": \"5.5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1989 DEC\",\n" +
                "      \"value\": \"5.5\",\n" +
                "      \"year\": \"1989\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 JAN\",\n" +
                "      \"value\": \"5.7\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 FEB\",\n" +
                "      \"value\": \"5.9\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 MAR\",\n" +
                "      \"value\": \"6\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 APR\",\n" +
                "      \"value\": \"6.4\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 MAY\",\n" +
                "      \"value\": \"6.8\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 JUN\",\n" +
                "      \"value\": \"6.9\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 JUL\",\n" +
                "      \"value\": \"6.8\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 AUG\",\n" +
                "      \"value\": \"7.7\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 SEP\",\n" +
                "      \"value\": \"8.1\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 OCT\",\n" +
                "      \"value\": \"8.1\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 NOV\",\n" +
                "      \"value\": \"7.8\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1990 DEC\",\n" +
                "      \"value\": \"7.6\",\n" +
                "      \"year\": \"1990\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 JAN\",\n" +
                "      \"value\": \"7.1\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 FEB\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 MAR\",\n" +
                "      \"value\": \"6.9\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 APR\",\n" +
                "      \"value\": \"8.5\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 MAY\",\n" +
                "      \"value\": \"8.2\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 JUN\",\n" +
                "      \"value\": \"8.4\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 JUL\",\n" +
                "      \"value\": \"8.3\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 AUG\",\n" +
                "      \"value\": \"7.7\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 SEP\",\n" +
                "      \"value\": \"7.1\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 OCT\",\n" +
                "      \"value\": \"6.8\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 NOV\",\n" +
                "      \"value\": \"7.1\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1991 DEC\",\n" +
                "      \"value\": \"7.2\",\n" +
                "      \"year\": \"1991\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 JAN\",\n" +
                "      \"value\": \"7\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 FEB\",\n" +
                "      \"value\": \"6.9\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 MAR\",\n" +
                "      \"value\": \"7.1\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 APR\",\n" +
                "      \"value\": \"4.7\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 MAY\",\n" +
                "      \"value\": \"4.3\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 JUN\",\n" +
                "      \"value\": \"3.8\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 JUL\",\n" +
                "      \"value\": \"3.6\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 AUG\",\n" +
                "      \"value\": \"3.2\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 SEP\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 OCT\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 NOV\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1992 DEC\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1992\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 JAN\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 FEB\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 MAR\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 APR\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 MAY\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 JUN\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 JUL\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 AUG\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 SEP\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 OCT\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 NOV\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1993 DEC\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1993\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 JAN\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 FEB\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 MAR\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 APR\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 MAY\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 JUN\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 JUL\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 AUG\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 SEP\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 OCT\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 NOV\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1994 DEC\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1994\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 JAN\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 FEB\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 MAR\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 APR\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 MAY\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 JUN\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 JUL\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 AUG\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 SEP\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 OCT\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 NOV\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1995 DEC\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"1995\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 JAN\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 FEB\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 MAR\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 APR\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 MAY\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 JUN\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 JUL\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 AUG\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 SEP\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 OCT\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 NOV\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1996 DEC\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"1996\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 JAN\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 FEB\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 MAR\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 APR\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 MAY\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 JUN\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 JUL\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 AUG\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 SEP\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 OCT\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 NOV\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1997 DEC\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1997\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 JAN\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 FEB\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 MAR\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 APR\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 MAY\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 JUN\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 JUL\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 AUG\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 SEP\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 OCT\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 NOV\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1998 DEC\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1998\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 JAN\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 FEB\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 MAR\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 APR\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 MAY\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 JUN\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 JUL\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 AUG\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 SEP\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 OCT\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 NOV\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"1999 DEC\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"1999\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 JAN\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 FEB\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 MAR\",\n" +
                "      \"value\": \"0.6\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 APR\",\n" +
                "      \"value\": \"0.6\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 MAY\",\n" +
                "      \"value\": \"0.5\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 JUN\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 JUL\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 AUG\",\n" +
                "      \"value\": \"0.6\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 SEP\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 OCT\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 NOV\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2000 DEC\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2000\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 JAN\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 FEB\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 MAR\",\n" +
                "      \"value\": \"0.9\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 APR\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 MAY\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 JUN\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 JUL\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 AUG\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 SEP\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 OCT\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 NOV\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2001 DEC\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2001\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 JAN\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 FEB\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 MAR\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 APR\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 MAY\",\n" +
                "      \"value\": \"0.8\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 JUN\",\n" +
                "      \"value\": \"0.6\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 JUL\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 AUG\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 SEP\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 OCT\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 NOV\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2002 DEC\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2002\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 JAN\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 FEB\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 MAR\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 APR\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 MAY\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 JUN\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 JUL\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 AUG\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 SEP\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 OCT\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 NOV\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2003 DEC\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2003\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 JAN\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 FEB\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 MAR\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 APR\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 MAY\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 JUN\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 JUL\",\n" +
                "      \"value\": \"1.4\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 AUG\",\n" +
                "      \"value\": \"1.3\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 SEP\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 OCT\",\n" +
                "      \"value\": \"1.2\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 NOV\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2004 DEC\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2004\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 JAN\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 FEB\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 MAR\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 APR\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 MAY\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 JUN\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 JUL\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 AUG\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 SEP\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 OCT\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 NOV\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2005 DEC\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2005\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 JAN\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 FEB\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 MAR\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 APR\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 MAY\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 JUN\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 JUL\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 AUG\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 SEP\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 OCT\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 NOV\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2006 DEC\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2006\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 JAN\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 FEB\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 MAR\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 APR\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 MAY\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 JUN\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 JUL\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 AUG\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 SEP\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 OCT\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 NOV\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2007 DEC\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2007\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 JAN\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 FEB\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 MAR\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 APR\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 MAY\",\n" +
                "      \"value\": \"3.3\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 JUN\",\n" +
                "      \"value\": \"3.8\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 JUL\",\n" +
                "      \"value\": \"4.4\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 AUG\",\n" +
                "      \"value\": \"4.7\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 SEP\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 OCT\",\n" +
                "      \"value\": \"4.5\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 NOV\",\n" +
                "      \"value\": \"4.1\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2008 DEC\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2008\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 JAN\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 FEB\",\n" +
                "      \"value\": \"3.2\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 MAR\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 APR\",\n" +
                "      \"value\": \"2.3\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 MAY\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 JUN\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 JUL\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 AUG\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 SEP\",\n" +
                "      \"value\": \"1.1\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 OCT\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 NOV\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2009 DEC\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"2009\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 JAN\",\n" +
                "      \"value\": \"3.5\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 FEB\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 MAR\",\n" +
                "      \"value\": \"3.4\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 APR\",\n" +
                "      \"value\": \"3.7\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 MAY\",\n" +
                "      \"value\": \"3.4\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 JUN\",\n" +
                "      \"value\": \"3.2\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 JUL\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 AUG\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 SEP\",\n" +
                "      \"value\": \"3.1\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 OCT\",\n" +
                "      \"value\": \"3.2\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 NOV\",\n" +
                "      \"value\": \"3.3\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2010 DEC\",\n" +
                "      \"value\": \"3.7\",\n" +
                "      \"year\": \"2010\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 JAN\",\n" +
                "      \"value\": \"4\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 FEB\",\n" +
                "      \"value\": \"4.4\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 MAR\",\n" +
                "      \"value\": \"4\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 APR\",\n" +
                "      \"value\": \"4.5\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 MAY\",\n" +
                "      \"value\": \"4.5\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 JUN\",\n" +
                "      \"value\": \"4.2\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 JUL\",\n" +
                "      \"value\": \"4.4\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 AUG\",\n" +
                "      \"value\": \"4.5\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 SEP\",\n" +
                "      \"value\": \"5.2\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 OCT\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 NOV\",\n" +
                "      \"value\": \"4.8\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2011 DEC\",\n" +
                "      \"value\": \"4.2\",\n" +
                "      \"year\": \"2011\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 JAN\",\n" +
                "      \"value\": \"3.6\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 FEB\",\n" +
                "      \"value\": \"3.4\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 MAR\",\n" +
                "      \"value\": \"3.5\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 APR\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 MAY\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 JUN\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 JUL\",\n" +
                "      \"value\": \"2.6\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 AUG\",\n" +
                "      \"value\": \"2.5\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 SEP\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 OCT\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 NOV\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2012 DEC\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2012\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 JAN\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 FEB\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 MAR\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 APR\",\n" +
                "      \"value\": \"2.4\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 MAY\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 JUN\",\n" +
                "      \"value\": \"2.9\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 JUL\",\n" +
                "      \"value\": \"2.8\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 AUG\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 SEP\",\n" +
                "      \"value\": \"2.7\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"September\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 OCT\",\n" +
                "      \"value\": \"2.2\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"October\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 NOV\",\n" +
                "      \"value\": \"2.1\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"November\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2013 DEC\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"year\": \"2013\",\n" +
                "      \"month\": \"December\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 JAN\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"January\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 FEB\",\n" +
                "      \"value\": \"1.7\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"February\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 MAR\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"March\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 APR\",\n" +
                "      \"value\": \"1.8\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"April\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 MAY\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"May\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 JUN\",\n" +
                "      \"value\": \"1.9\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"June\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 JUL\",\n" +
                "      \"value\": \"1.6\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"July\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2014 AUG\",\n" +
                "      \"value\": \"1.5\",\n" +
                "      \"year\": \"2014\",\n" +
                "      \"month\": \"August\",\n" +
                "      \"sourceDataset\": \"MM23\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"sourceDatasets\": [\n" +
                "    \"MM23\"\n" +
                "  ],\n" +
                "  \"section\": {\n" +
                "    \"markdown\": \"Consumer price indices are important indicators of how the UK economy is performing.\\n\\nThe indices are used in many ways by the government, businesses, and society in general. They can affect interest rates, tax allowances, wages, state benefits, pensions, maintenance, contracts and many other payments. They also show the impact of inflation on family budgets and affect the value of the pound in your pocket.\"\n" +
                "  },\n" +
                "  \"notes\": [\n" +
                "    \"Inflation rates prior to 1997 and index levels prior to 1996 are estimated.\\n\",\n" +
                "    null\n" +
                "  ],\n" +
                "  \"relatedDocuments\": [\n" +
                "    {\n" +
                "      \"uri\": \"/economy/inflationandpriceindices/bulletins/consumerpriceinflationjune2014\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"type\": \"timeseries\",\n" +
                "  \"uri\": \"/economy/inflationandpriceindices/timeseries/d7g7\",\n" +
                "  \"breadcrumb\": [\n" +
                "    {\n" +
                "      \"uri\": \"/\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"uri\": \"/economy\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"uri\": \"/economy/inflationandpriceindices\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"description\": {\n" +
                "    \"date\": \"September 2014\",\n" +
                "    \"number\": \"1.2\",\n" +
                "    \"keyNote\": \"Percentage change over 12 months\",\n" +
                "    \"cdid\": \"D7G7\",\n" +
                "    \"unit\": \"%\",\n" +
                "    \"preUnit\": \"\",\n" +
                "    \"source\": \"Office for National Statistics\",\n" +
                "    \"nationalStatistic\": true,\n" +
                "    \"title\": \"CPI: Consumer Prices Index (% change)\"\n" +
                "  }\n" +
                "}");
        System.out.println(new SparkLine(timeSeries).toJson());
    }

}
