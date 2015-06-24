var linechart = function(timeseries) {
	var chart = {};
	var years = false;
	var months = false;
	var querters = false;
	var showYears = false;
	var showMonths = false;
	var showQuarters = false;
	var chartContainer = $('[data-chart]');

	initialize();

	function initialize() {
		chart = getLinechartConfig(timeseries);
		showYears = isNotEmpty(timeseries.years);
		showMonths = isNotEmpty(timeseries.months);
		showQuarters = isNotEmpty(timeseries.quarters);
		var frequency = '';

		if (!(showYears || showMonths || showQuarters)) {
			console.debug('No data found');
			// return; // No data to render chart with
		}

		if (showMonths) {
			timeseries.months = formatData(timeseries.months);
			frequency = 'months';
		}

		if (showQuarters) {
			timeseries.quarters = formatData(timeseries.quarters)
			frequency = 'quarters';
		}

		if (showYears) {
			timeseries.years = formatData(timeseries.years)
			frequency = 'years';
		}
		changeFrequency(frequency);

		chartControls = new ChartControls();
		chartControls.initialize();
	}


	function renderChart() {
		console.debug('Rendering chart');
		console.debug(chart);
		chartContainer.highcharts(chart);
	}

	function changeFrequency(frequency) {
		console.log(frequency);
		var data = timeseries[frequency];
		chart.series[0].data = data.values;
		chart.xAxis.tickInterval = tickInterval(data.values.length);
		chart.yAxis.min = data.min;
		renderChart(chart);
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
			data.min = min;
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


	function ChartControls() {

		var element = $('[data-chart-controls]');

		function initialize() {

			bindFrequencyChangeButtons();
			bindTypeChangeButtons();
			bindLinkEvents();
			setCollapsible();
		}

		function bindFrequencyChangeButtons() {

			/*
			 * Add click handlers to the controls
			 */
			$('[data-chart-controls-scale]', element).on('click', function(e, data) {
				var frequency = e.toElement.value
				toggleSelectedButton();
				changeFrequency(frequency);
			});
		}

		function bindTypeChangeButtons() {

			$('[data-chart-controls-type]', element).on('click', function(e, data) {
				toggleSelectedButton();

				$('[data-chart]').addClass('js-hidden');
				$('[data-chart-data-id="' + $(this).val() + '"]').removeClass('js-hidden');

			});
		}

		function bindShowButton() {
			/*
			 * Add event to submit button that applies the filter
			 */
			$('[data-chart-controls-submit]', element).on('click', function(e, data) {

				data = data || {};

				e.preventDefault();

				_.defaults(data, {
					custom: true
				});

				if (data.custom !== false) {
					toggleSelectedLink($('.link-complex', self.element));
				}

				//updateFilter();
			});

		}


		function bindLinkEvents() {

			$('[data-chart-controls-range]').on('click', function(e) {

				var elem = $(this);
				var filterDate;
				var fromYear;
				var fromMonth;

				console.log(e);
				e.preventDefault();

				toggleSelectedLink(elem);

				/*
				 * Work out what the dates are
				 */

				switch (elem.data('chart-controls-range')) {
					case '10yr':
						filterDate = moment().subtract(10, 'years');

						fromMonth = filterDate.month() + 1;
						fromYear = filterDate.year();

						break;

					case '5yr':
						filterDate = moment().subtract(5, 'years');

						fromMonth = filterDate.month() + 1;
						fromYear = filterDate.year();

						break;

					case 'all':

						fromMonth = $('[data-chart-controls-from-month] option:first-child', element).val();
						fromYear = $('[data-chart-controls-from-year] option:first-child', element).val();

						break;
				}

				/*
				 * Set the select options
				 */
				$('[data-chart-controls-from-month]', element).find('option[value="' + fromMonth + '"]').attr('selected', true);
				$('[data-chart-controls-from-year]', element).find('option[value="' + fromYear + '"]').attr('selected', true);
				$('[data-chart-controls-to-month]', element).find('option[value="' + (moment().month() + 1) + '"]').attr('selected', true);
				$('[data-chart-controls-to-year]', element).find('option[value="' + moment().year() + '"]').attr('selected', true);

				/*
				 * Trigger a click
				 */
				$('[data-chart-controls-submit]').trigger('click', {
					custom: false
				});
			});
		};

		/**
		 * Add the collape / expand behaviour to the custom date filter
		 */
		function setCollapsible() {

			var customControl = $('[data-chart-control-custom-range]', element);
			var elem;
			var target;

			$('[data-chart-control-custom-trigger-for]', customControl).on('click', function(e) {
				e.preventDefault();
				elem = $(this);
				target = $('.' + elem.data('chart-control-custom-trigger-for'));

				if (customControl.data('chart-control-custom-expanded') == true) {
					target.slideUp('fast', function() {
						customControl.data('chart-control-custom-expanded', false);
						customControl.removeClass('chart-area__controls__custom--active');
						$('.icon-up-open-big', customControl)
							.removeClass('icon-up-open-big')
							.addClass('icon-down-open-big');
					});

				} else {
					customControl.addClass('chart-area__controls__custom--active');

					// remove our nice no-js friendly hiding now we know js is active
					target.hide().removeClass('js-hidden');

					target.slideDown('fast', function() {
						customControl.data('chart-control-custom-expanded', true);
						$('.icon-down-open-big', customControl)
							.removeClass('icon-down-open-big')
							.addClass('icon-up-open-big');

					});
				}

			});
		};

		function toggleSelectedLink(clickedElem) {
			$('a', element).removeClass('chart-area__controls__active');
			clickedElem.addClass('chart-area__controls__active');
		};

		function toggleSelectedButton() {

			var selectedElement = $('input:checked', element);
			$('label', element).removeClass('btn--secondary--active');

			selectedElement.each(function() {
				$(this).parent('label').addClass('btn--secondary--active');
			});

		};

		$.extend(this, {
			initialize: initialize
		});

	}



	$.extend(this, {})
	return this;

};