package com.github.onsdigital.highcharts;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.ColorReference;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.color.RgbaColor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bren on 18/06/15.
 */
public class LineChart extends BaseChart {

    public LineChart(TimeSeries timeSeries) {
        super(SeriesType.LINE, timeSeries);
    }

    @Override
    protected void configureChart(Options options) {

        ChartOptions chart = options.getChart();
        chart.setStyle(new CssStyle().setProperty("overflow", "visible"));


        /*Colors*/
        options.setColors(Arrays.asList(new ColorReference[]{
                new HexColor("#007dc3"),
                new HexColor("#409ed2"),
                new HexColor("#7fbee1"),
                new HexColor("#007dc3"),
                new HexColor("#409ed2"),
                new HexColor("#7fbee1")
        }));


        /*Navigaton*/
        options.setNavigation(new Navigation().setButtonOptions(new ButtonOptions().setEnabled(false)));

        /*Title*/
        options.setTitle(new Title(getTimeSeries().getDescription().getTitle()));
        options.setSubtitle(new Title("Source: " + getTimeSeries().getDescription().getSource())
                        .setFloating(true)
                        .setAlign(HorizontalAlignment.RIGHT)
                        .setX(0)
                        .setY(10)
                        .setVerticalAlign(VerticalAlignment.BOTTOM)
        );

        /*xAxis*/
        Axis xAxis = new Axis();
        options.setxAxis(xAxis);
        xAxis.setCategories(new ArrayList<String>());
        xAxis.setTickInterval(Float.valueOf(resolveTickInterval(getData())));
        Labels labels = new Labels();
        labels.setFormatter(new Function().setFunction(
                        "\t                            var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);\n" +
                        "\t                            var response = \"\";\n" +
                        "\t                            if (w < 768) {\n" +
                        "\t                                if (this.isFirst) {\n" +
                        "\t                                    count = 0;\n" +
                        "\t                                }\n" +
                        "\t                                if (count % 3 === 0) {\n" +
                        "\t                                    response = this.value;\n" +
                        "\t                                }\n" +
                        "\t                                count++;\n" +
                        "\t                            } else {\n" +
                        "\t                                response = this.value;\n" +
                        "\t                            }\n" +
                        "\t                            return response;\n"
        ));
        xAxis.setLabels(labels);
        xAxis.setTickmarkPlacement(TickmarkPlacement.ON);
//        xAxis.setLineColor(); //TODO: How to set to transperent?

        /*yAxis*/
        Axis yAxis = new Axis();
        options.setyAxis(yAxis);
        yAxis.setTitle(new Title(getTimeSeries().getDescription().getPreUnit() + " " + getTimeSeries().getDescription().getUnit())
                .setAlign(HorizontalAlignment.HIGH).setRotation(0).setX(10).setY(-10));

        //labels
//        Labels yLabels = new Labels();
//        yAxis.setLabels(yLabels);
//        yLabels.setEnabled(false);

        /*legend*/
//        options.setLegend(new Legend().setEnabled(false));

        /*Exporting*/
        options.setExporting(new ExportingOptions().setEnabled(false));

        /*plot options*/
        PlotOptionsChoice plotOptionsChoice = new PlotOptionsChoice();
        options.setPlotOptions(plotOptionsChoice);
        //Series
        plotOptionsChoice.setSeries(new PlotOptions()
                .setShadow(false)
                .setStates(new StatesChoice()
                        .setHover(new State()
                                        //TODO: Set lineWidthPlus?
                                        .setEnabled(true)
                                        .setLineWidth(3)
                        ))
                .setMarker(new Marker()
                                .setEnabled(true)
                                .setFillColor(new NullColor())
                                .setLineWidth(3)
                ));


        /*Set series options*/
        options.getSeries().get(0)
                .setName("Hey")
                .setDashStyle(GridLineDashStyle.SOLID)
                .setName("")
                .setMarker(new Marker()
                        .setStates(new StatesChoice()
                                        .setHover(new State()
                                                        .setFillColor(new HexColor("#007dc3")
                                                        )
                                        )
                        )
                        .setSymbol(new Symbol(Symbol.PredefinedSymbol.CIRCLE)));


        options.setTooltip(
                new Tooltip()
                        .setShared(true)
                        .setStyle(new CssStyle().setProperty("width", "150px"))
                        .setXCrosshair(new Crosshair().setWidth(2).setColor(new HexColor("#f37121")))
                        .setYCrosshair(new Crosshair().setWidth(2).setColor(new HexColor("#f37121")))
                        .setPositioner(new Function().setFunction(
                                " var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);\n" +
                                        "                        var points = {\n" +
                                        "                            x: 30,\n" +
                                        "                            y: 42\n" +
                                        "                        };\n" +
                                        "                        var tooltipX, tooltipY;\n" +
                                        "                        var chart = Highcharts.charts[Highcharts.charts.length - 1];\n" +
                                        "                        if (w > 768) {\n" +
                                        "\n" +
                                        "                            if (point.plotX + labelWidth > chart.plotWidth) {\n" +
                                        "                                tooltipX = point.plotX + chart.plotLeft - labelWidth - 20;\n" +
                                        "                                $(\"#custom-tooltip\").removeClass('tooltip-left');\n" +
                                        "                            } else {\n" +
                                        "                                tooltipX = point.plotX + chart.plotLeft + 20;\n" +
                                        "                                $(\"#custom-tooltip\").removeClass('tooltip-right');\n" +
                                        "                            }\n" +
                                        "\n" +
                                        "                            tooltipY = 50;\n" +
                                        "                            points = {\n" +
                                        "                                x: tooltipX,\n" +
                                        "                                y: tooltipY\n" +
                                        "                            };\n" +
                                        "                        } else {\n" +
                                        "                            $(\"#custom-tooltip\").removeClass('tooltip-left');\n" +
                                        "                            $(\"#custom-tooltip\").removeClass('tooltip-right');\n" +
                                        "                        }\n" +
                                        "\n" +
                                        "                        return points;"
                        ))
                        .setFormatter(new Function().setFunction(
                                "  var id1 = '<div id=\"custom-tooltip\" class=\"tooltip-left tooltip-right\">';\n" +
                                        "                        var block = id1 + \"<div class='sidebar' >\";\n" +
                                        "                        var title = '<b class=\"title\">' + this.points[0].key + ': </b><br/>';\n" +
                                        "                        var symbol = ['<div class=\"circle\">●</div>', '<div class=\"square\">■</div>', '<div class=\"diamond\">♦</div>', '<div class=\"triangle\">▲</div>', '<div class=\"triangle\">▼</div>'];\n" +
                                        "\n" +
                                        "                        var content = block + \"<div class='title'>&nbsp;</div>\";\n" +
                                        "\n" +
                                        "                        // symbols\n" +
                                        "                        $.each(this.points, function(i, val) {\n" +
                                        "                            content += symbol[i];\n" +
                                        "                        });\n" +
                                        "\n" +
                                        "                        content += \"</div>\";\n" +
                                        "                        content += \"<div class='mainText'>\";\n" +
                                        "                        content += title;\n" +
                                        "\n" +
                                        "                        // series names and values\n" +
                                        "                        $.each(this.points, function(i, val) {\n" +
                                        "                            content += '<div class=\"tiptext\"><i>' + val.point.series.chart.series[i].name + \"</i><br/><b>Value: \" + val.point.series.chart.series[i].options.preUnit + val.y + \" \" + val.point.series.chart.series[i].options.unit + '</b></div>';\n" +
                                        "                        });\n" +
                                        "                        content += \"</div>\";\n" +
                                        "                        return content;"
                        ))
                        .setBackgroundColor(new RgbaColor(255, 255, 255, 0f))
                        .setBorderWidth(0)
                        .setBorderColor(new RgbaColor(255, 255, 255, 0f))
                        .setShadow(false)
                        .setUseHTML(true)
        );

    }
}
