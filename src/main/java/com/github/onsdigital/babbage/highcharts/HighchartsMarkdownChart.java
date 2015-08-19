package com.github.onsdigital.babbage.highcharts;

import java.util.*;

public class HighchartsMarkdownChart {

    public Chart chart;
    public List<String> colors;
    public List<Series> series;
    public Title title = new Title();
    public Axis yAxis;
    public Axis xAxis;
    public Labels labels;
    public Legend legend = new Legend();
    public PlotOptions plotOptions = new PlotOptions();
    public Tooltip tooltip;
    public Credits credits = new Credits();

    private boolean stacked = false;

    public HighchartsMarkdownChart(com.github.onsdigital.content.page.statistics.document.figure.chart.Chart markdownChart) {

        this.chart = new Chart();
        chart.width = 600;
        Double heightCalculated = chart.width * Double.parseDouble(markdownChart.getAspectRatio());
        chart.height = (heightCalculated.intValue());

        this.colors = Arrays.asList("#274796", "#F5942F", "#E73F40", "#7BCAE2", "#979796", "#E9E117", "#74B630", "#674796", "#BD5B9E");

        mapSeries(markdownChart);

        yAxis = new Axis();
        xAxis = new Axis();

        xAxis.categories = Arrays.asList(markdownChart.getCategories());
        xAxis.tickInterval = markdownChart.getLabelInterval();

        yAxis.labels = new HashMap<String, String>();
        yAxis.labels.put("format", "{value:,f}");

        if (markdownChart.isRotated()) {

            yAxis.title = new Title();
            yAxis.title.align = "high";
            yAxis.title.text = markdownChart.getUnit();

        } else {

            yAxis.title = new Title();
            yAxis.title.text = "";

            labels = new Labels();
            Label chartUnitLabel = new Label();
            chartUnitLabel.html = markdownChart.getUnit();
            chartUnitLabel.style = new HashMap<>();
            chartUnitLabel.style.put("top", "0px");
            chartUnitLabel.style.put("left", "0px");
            labels.items.add(chartUnitLabel);
        }

        tooltip = new Tooltip();
        tooltip.valueDecimals = markdownChart.getDecimalPlaces();

        if (markdownChart.isBarLine()) {
            this.plotOptions.column.stacking = "normal";
        }
    }

    // convert the series data from our json format to highcharts forma
    private void mapSeries(com.github.onsdigital.content.page.statistics.document.figure.chart.Chart markdownChart) {
        this.series = new ArrayList<>();

        for (String seriesName : markdownChart.getSeries()) {

            Series thisSeries = new Series();
            thisSeries.name = seriesName;
            thisSeries.data = new ArrayList<>();

            // map the chart type to each series. If its a barline then evaluate the type for each series.
            thisSeries.type = mapType(markdownChart.getChartType());
            if (markdownChart.isBarLine()) {
                thisSeries.type = mapType(markdownChart.getChartTypes().get(seriesName));

                if (thisSeries.type.equals("line")) {
                    thisSeries.zIndex = 100; // ensure lines are rendered on top of columns.
                }
            }

            for (Map<String, String> seriesData : markdownChart.getData()) {
                Double value = null;
                try {
                    value = Double.parseDouble(seriesData.get(seriesName));
                } catch (NumberFormatException | NullPointerException exception) {
                    // leave value as null if we cannot parse it.
                }

                thisSeries.data.add(value);
            }

            if (markdownChart.isBarLine()) {
                if (Arrays.asList(markdownChart.getGroups()[0]).contains(seriesName)) {
                    thisSeries.stack = "group1"; // we only support one group.
                    stacked = true;
                } else {
                    thisSeries.stack = seriesName; // set a unique stack group to not stack
                }
            }

            this.series.add(thisSeries);
        }
    }

    private String mapType(String chartType) {
        if (chartType.equals("rotated")) {
            return "bar";
        } else if (chartType.equals("barline")) {
            return "column";
        } else if (chartType.equals("bar")) {
            return "column";
        } else {
            return chartType;
        }
    }

    class Chart {
        public String renderTo;
        public int height;
        public int width;
    }

    class Title {
        public String text = "";
        public String align;
    }

    class Series {
        public String name;
        public String type;
        public String stack;
        public List<Double> data;
        public int zIndex;
    }

    class Axis {
        public Title title;
        public List<String> categories;
        public String tickInterval;
        public Map<String, String> labels;
    }

    class Labels {
        public List<Label> items = new ArrayList<>();
    }

    class Label {
        public String html;
        public Map<String, String> style;
    }

    class PlotOptions {
        public SeriesOptions series = new SeriesOptions();
        public LineOptions line = new LineOptions();
        public ColumnOptions column = new ColumnOptions();
    }

    class SeriesOptions {
        public boolean animation = false;
        public double pointPadding = 0;
        public double groupPadding = 0.1;
    }

    class LineOptions {
        public int lineWidth = 1;
        public LineMarker marker = new LineMarker();
    }

    class ColumnOptions {
        public String stacking;
    }

    class LineMarker {
        public int radius = 2;
        public String symbol = "circle";
    }

    class Legend {
        public String verticalAlign = "top";
    }

    class Tooltip {
        public String valueDecimals;
        public boolean shared = true;
    }

    class Credits {
        public boolean enabled = false;
    }
}