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


    if ($("body").hasClass("viewport-sm")) {
        console.log('SM');
    }else if ($("body").hasClass("viewport-md")) {
        console.log('MD');
    }else if ($("body").hasClass("viewport-lg")) {
        console.log('LG');
    }

    chartContainer.each(function() {
        var $this = $(this);
        var id = $this.attr('id');
        var chartId = $this.data('filename');
        var chartWidth = $this.width();
        var chartUri = $this.data('uri'); 

        $this.empty();

        //Read chart configuration from server using container's width
        var jqxhr = $.get("/chartconfig", {
                uri: chartUri,
                width: chartWidth
            },
            function() {
                var chartConfig = window["chart-" + chartId];
                if (chartConfig) {
                    // small multiples have an attribute to show specifc series
                    var display = $this.data('series');
                    var array = id.split("-");// get 

                    if(display){
                        //loop through series and create mini-charts
                        var tempSeries = chartConfig.series;
                        chartConfig.chart.height = chartWidth/3;
                        chartConfig.chart.width = chartWidth/3;
                        chartConfig.series = [tempSeries[array[2]]];
                        chartConfig.chart.renderTo = id;
                        new Highcharts.Chart(chartConfig);

                    }else{
                        // Build chart from config endpoint
                        chartConfig.chart.renderTo = id;
                        new Highcharts.Chart(chartConfig);
                    }

                    delete window["chart-" + chartId]; //clear data from window object after rendering



                }
            }, "script");

    });
});
