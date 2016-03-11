$(function() {
    // Global options for markdown charts
    Highcharts.setOptions({
        lang: {
            thousandsSep: ','
        }
    });

    var chartContainer = $(".markdown-chart");
    if (!chartContainer.length) {
        return;
    }

    chartContainer.each(function() {
        var $this = $(this);
        var id = $this.attr('id');
        var chartId = $this.data('filename');
        var chartWidth = $this.width();
        var chartUri = $this.data('uri'); //= $this.data('uri');
        $this.empty();

        //Read chart configuration from server using container's width
        var jqxhr = $.get("/chartconfig", {
                uri: chartUri,
                width: chartWidth
            },
            function() {
                var chartConfig = window["chart-" + chartId];
                if (chartConfig) {
                    // Build chart from config endpoint
                    chartConfig.chart.renderTo = id;
                    new Highcharts.Chart(chartConfig);
                    delete window["chart-" + chartId]; //clear data from window object after rendering
                }
            }, "script");

    });
});
