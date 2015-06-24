var linechart = function(timeseries, containerId) {

	var chartContainer = $('#' + containerId);
	var chart = {};
	var years = false;
	var months = false;
	var querters = false;

	initialize();

	function initialize() {
		chart = getLinechartConfig(timeseries);
		years = isNotEmpty(timeseries.years);
		months = isNotEmpty(timeseries.months);
		quarters = isNotEmpty(timeseries.quarters);
		var frequency = '';

		if (!(years || months || quarters)) {
			console.debug('No data found');
			return; // No data to render chart with
		}

		if (months) {
			timeseries.months = formatData(timeseries.months);
			frequency = 'months';
		}

		if (quarters) {
			timeseries.quarters = formatData(timeseries.quarters)
			frequency = 'quarters';
		}

		if (years) {
			timeseries.years = formatData(timeseries.years)
			frequency = 'years';
		}
		changeFrequency(frequency);
		renderChart(chart);
	}


	function renderChart() {
		console.debug('Rendering chart');
		console.debug(chart);
		chartContainer.highcharts(chart);
	}

	function changeFrequency(frequency) {
		console.log(frequency);
		console.log(timeseries);
		var data = timeseries[frequency];
		chart.series[0].data = data.values;
		chart.xAxis.tickInterval = tickInterval(data.values.length);
		chart.yAxis.min = data.min;
	}

	function tickInterval(length) {
		console.log(length);
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

	//Format data into high charts compatible format
	function formatData(timeseriesValues) {
		var data = {
			values: [],
			years: []
		};
		var current;
		var i;
		var min;

		for (i = 0; i < timeseriesValues.length; i++) {
			current = timeseriesValues[i]
			if (current.value < min) {
				min = current.value;
			}
			data.min=min;
			data.values.push(enrichData(current, i));
			data.years.push(current.year);
		}
		toUnique(data.years);
		return data
	}

	function enrichData(timeseriesValue) {
		var quarter = timeseriesValue.quarter;
		var year = timeseriesValue.year;
		var month = timeseriesValue.month;

		timeseriesValue.y = +timeseriesValue.value; //Cast to number
		timeseriesValue.value = +(year + (quarter ? quarterVal(quarter) : '') + (month ? monthVal(month) : ''));
		timeseriesValue.name = timeseriesValue.date; //Appears on x axis
		delete timeseriesValue.date;

		return timeseriesValue;
	}



	function monthVal(mon) {
		switch (mon.slice(0, 3).toUpperCase()) {
			case 'JAN':
				return '01'
			case 'FEB':
				return '02'
			case 'MAR':
				return '03'
			case 'APR':
				return '04'
			case 'MAY':
				return '05'
			case 'JUN':
				return '06'
			case 'JUL':
				return '07'
			case 'AUG':
				return '08'
			case 'SEP':
				return '09'
			case 'OCT':
				return '10'
			case 'NOV':
				return '11'
			case 'DEC':
				return '12'
			default:
				throw 'Invalid Month:' + mon

		}
	}

	function quarterVal(quarter) {
		switch (quarter) {
			case 'Q1':
				return 1
			case 'Q2':
				return 2
			case 'Q3':
				return 3
			case 'Q4':
				return 4
			default:
				throw 'Invalid Quarter:' + quarter

		}
	}

	//Check if arrray is not empty
	function isNotEmpty(array) {
		return (array && array.length > 0)
	}


	//Remove duplicate values in given array
	function toUnique(a) { //array,placeholder,placeholder
		var b = a.length;
		var c
		while (c = --b) {
			while (c--) {
				a[b] !== a[c] || a.splice(c, 1);
			}
		}
	}


	$.extend(chart, {})

	return this;

};