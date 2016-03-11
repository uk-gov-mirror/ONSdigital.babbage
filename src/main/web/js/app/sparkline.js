
function getSparkline() {
    var chartContainer = $(".sparkline");
    if (chartContainer.length) {
        chartContainer.each(function() {
            var $this = $(this);
            var uri = $this.data('uri');
            $this.empty();
            $.getJSON(uri + '/data?series', function(timeseries) {
                // console.log("Successfully read timseries data");
                renderSparkline(timeseries, $this);
            }).fail(function(d, textStatus, error) {
                // console.error("Failed reading timeseries, status: " + textStatus + ", error: " + error)
            });
        });
    }
}

var renderSparkline = function(data, chartContainer) {
    var sparklinename = 'sparkline' + data.description.cdid;
    var chart = window[sparklinename];
	renderChart();

    function renderChart() {
        for (var i = data.series.length - 1; i >= 0; i--) {
            var y = data.series[i].y;
            data.series[i].y = y ? y : null; // highcharts does not play well with undefined y value
        }
        chart.series[0].data = data.series;
        chartContainer.highcharts(chart);
    }
};

$(function() {
    getSparkline();
});
