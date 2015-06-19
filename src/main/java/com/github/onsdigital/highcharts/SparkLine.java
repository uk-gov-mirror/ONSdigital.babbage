package com.github.onsdigital.highcharts;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by bren on 17/06/15.
 */
public class SparkLine extends BaseChart {

    public SparkLine(TimeSeries timeSeries) {
        super(SeriesType.AREA, timeSeries);
    }

    public void configureChart(Options options) {

        ChartOptions chart = options.getChart();
        chart.setBorderWidth(0);
//        chart.setMargin(Arrays.asList(new Integer[]{0, 0, 0, 0}));
        chart.setStyle(new CssStyle().setProperty("overflow", "visible"));

        /*Title*/
        options.setTitle(new Title(""));
        options.setSubtitle(new Title("").setY(110));

        /*xAxis*/
        Axis xAxis = new Axis();
        options.setxAxis(xAxis);
        xAxis.setCategories(new ArrayList<String>());
        xAxis.setTickInterval(Float.valueOf(resolveTickInterval(getData())));
        Labels labels = new Labels();
        labels.setFormatter(new Function().setFunction(
                "if (this.isFirst) " + "{ " + "return this.value " + "} " + " if (this.isLast) { " + " return this.value " + "}"));
        labels.setStep(1);
        xAxis.setLabels(labels);
        xAxis.setTickLength(0);
//        xAxis.setLineColor(); //TODO: How to set to transperent?

        /*yAxis*/
        Axis yAxis = new Axis();
        yAxis.setEndOnTick(false);
        yAxis.setStartOnTick(false);
        //labels
        Labels yLabels = new Labels();
        yAxis.setLabels(yLabels);
        yLabels.setEnabled(false);

        yAxis.setGridLineWidth(0); //TODO: Need to set tick positions instead
        yAxis.setTitle(new Title(""));
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
        plotOptionsChoice.setSeries(new PlotOptions().setAnimation(false)
                .setLineWidth(1)
                .setShadow(false)
                .setStates(new StatesChoice()
                        .setHover(new State()
                                .setLineWidth(1)))
//                .setMarker(new Marker()
//                        .setRadius(1)
//                )
//                .setStates(new StatesChoice()
//                        .setHover(new State()
//                                .setRadius(2)))
//                .setFillOpacity(0.25f)
                .setEnableMouseTracking(false));


        /*Set series options*/
        options.getSeries().get(0)
                .setDashStyle(GridLineDashStyle.SOLID)
                .setName("")
                .setMarker(new Marker()
                        .setSymbol(new Symbol(Symbol.PredefinedSymbol.CIRCLE)));


    }

}
