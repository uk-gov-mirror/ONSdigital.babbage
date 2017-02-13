$(function() {
    var viewport;
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

    //set the annotation and chart size based on the viewport

    if ($("body").hasClass("viewport-sm")) {
        viewport = 'sm';
    }else if ($("body").hasClass("viewport-md")) {
        viewport = 'md';
    }else  {
        viewport = 'lg';
    }
    console.log('viewport ' + viewport);



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
                    var array = id.split("-");

                    //adjust size and notes to match viewport
                    
                    //console.log(window["chart-" + chartId]);

                    var aspectRatio = 1;
                    //if we have devices
                    if(chartConfig.devices){
                        if(chartConfig.devices[viewport]){
                            console.log(chartConfig);
                            //set the aspect ratio
                            aspectRatio = chartConfig.devices[viewport].aspectRatio;
                            console.log("SET VIEWPORT FOR THE HEART OF THE SUN. " + aspectRatio);

                            //loop thru and update annotations if reqd
                            if(chartConfig.annotations.length>0){
                                $.each(chartConfig.annotations, function(idx, itm){
                                    console.log(chartConfig.annotations);
                                    chartConfig.annotations[idx].x = chartConfig.annotations[idx]['position_'+viewport].x;
                                    chartConfig.annotations[idx].y = chartConfig.annotations[idx]['position_'+viewport].y;
                                })
                            }
                            //loop thru plotline/plotbands
                            if(chartConfig.xAxis.plotLines.length>0){
                                $.each(chartConfig.xAxis.plotLines, function(idx, itm){
                                    console.log(chartConfig.xAxis.plotLines);
                                    chartConfig.xAxis.plotLines[idx].value = chartConfig.xAxis.plotLines[idx]['position_'+viewport].x;
                                })
                            }
                            //loop thru plotline/plotbands
                            if(chartConfig.xAxis.plotBands.length>0){
                                $.each(chartConfig.xAxis.plotBands, function(idx, itm){
                                    console.log(chartConfig.xAxis.plotBands);
                                    chartConfig.xAxis.plotBands[idx].value = chartConfig.xAxis.plotBands[idx]['position_'+viewport].x;
                                })
                            }
                            //loop thru plotline/plotbands
                            if(chartConfig.yAxis.plotLines.length>0){
                                $.each(chartConfig.yAxis.plotLines, function(idx, itm){
                                    console.log(chartConfig.xAxis.plotLines);
                                    chartConfig.yAxis.plotLines[idx].value = chartConfig.yAxis.plotLines[idx]['position_'+viewport].y;
                                })
                            }
                            //loop thru plotline/plotbands
                            if(chartConfig.yAxis.plotBands.length>0){
                                $.each(chartConfig.yAxis.plotBands, function(idx, itm){
                                    console.log(chartConfig.yAxis.plotBands);
                                    chartConfig.yAxis.plotBands[idx].value = chartConfig.yAxis.plotBands[idx]['position_'+viewport].y;
                                })
                            }

                        }
                    }
                    chartConfig.chart.width = chartConfig.chart.width * aspectRatio;

                    if(display){
                        //loop through series and create mini-charts
                        var tempSeries = chartConfig.series;
                        chartConfig.chart.height = chartWidth;
                        
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
