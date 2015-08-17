var renderSparkline = function(data) {
	var sparklinename = 'sparkline' + data.description.cdid;
	var chart = window[sparklinename];
	var chartContainer = $('[data-sparkline-'+data.description.cdid + ']');
	renderChart();

	function renderChart() {
		chart.series[0].data = data.series;
		chartContainer.highcharts(chart);
	}

};