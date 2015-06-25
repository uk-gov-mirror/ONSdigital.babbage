//  Ugliest code ever, copy paste stuff from pattern-library and alpha site, hence a bit messy

var linechart = function(timeseries) {
	var chart = {};
	chart.years = false;
	chart.months = false;
	chart.quarters = false;
	var chartContainer = $('[data-chart]');
	var currentFrequency;
	var currentDisplay = 'chart'; //chart or table
	var currentData;
	var currentFilter = 'all'; //10yr, 5yr, all, custom
	var table = chartContainer.find('[data-table]').detach();

	initialize();

	function initialize() {
		chart = getLinechartConfig(timeseries);
		chart.years = isNotEmpty(timeseries.years);
		chart.months = isNotEmpty(timeseries.months);
		chart.quarters = isNotEmpty(timeseries.quarters);
		chartControls = new ChartControls();

		var frequency = '';

		if (!(chart.years || chart.months || chart.quarters)) {
			console.debug('No data found');
			// return; // No data to render chart with
		}

		if (chart.months) {
			timeseries.months = formatData(timeseries.months);
			frequency = 'months';
		}

		if (chart.quarters) {
			timeseries.quarters = formatData(timeseries.quarters)
			frequency = 'quarters';
		}

		if (chart.years) {
			timeseries.years = formatData(timeseries.years)
			frequency = 'years';
		}
		changeFrequency(frequency);
		chartControls.initialize();
	}

	function changeFrequency(frequency) {
		if (currentFrequency == frequency) {
			return;
		}
		currentFrequency = frequency;
		currentData = getAllData();
		chartControls.changeDates();
		filter();
	}

	function filter() {
		//Filter
		var data = getAllData();
		if (currentFilter === 'all') {
			console.log("Showing all");
			currentData = data;
		} else {
			var filter = chartControls.getFilterValues();
			var from = filter.start.year + (isQuarters() ? filter.start.quarter : '') + (isMonths() ? filter.start.month : '');
			var to = filter.end.year + (isQuarters() ? filter.end.quarter : '') + (isMonths() ? filter.end.month : '');
			from = +from; //Cast to number
			to = +to;

			try {
				$('.chart-area__controls__custom__errors').empty();
				validateFilter(from, to);
			} catch (err) {
				$('<p>' + err.message + '</p>').appendTo('.chart-area__controls__custom__errors');
				return;
			}

			var filteredData = {
				values: [],
				min: undefined
			}

			var min = undefined;
			for (i = 0; i < data.values.length; i++) {
				current = data.values[i]
				if (current.value >= from && current.value <= to) {
					filteredData.values.push(current)
					if (!min || current.y < min) {
						min = current.y;
					}
				}
			}
			currentData = filteredData;
		}
		render();
	}

	function render() {
		chartContainer.empty(); //Clear container
		if (currentDisplay === 'chart') {
			renderChart();
		} else {
			renderTable();
		}
	}

	function renderTable() {
		var tbody = table.find('tbody');
		tbody.empty();
		for (i = 0; i < data.values.length; i++) {
			current = data.values[i];
			tbody.append('<tr></tr>').append()
		}

	}


	function renderChart() {
		console.log(currentData);
		chart.series[0].data = currentData.values;
		chart.xAxis.tickInterval = tickInterval(currentData.values.length);
		var min = currentData.min;
		if (min < 0) {
			min = min - 1;
		} else {
			min = 0;
		}

		chart.yAxis.min =  min;
		chartContainer.highcharts(chart);
	}


	function validateFilter(from, to) {
		if (from === to) {
			throw new Error('Sorry, the start date and end date cannot be the same');
		} else if (to < from) {
			throw new Error('Sorry, the chosen date range is not valid');
		}
	}

	function tickInterval(length) {
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
			if (!min || +current.value < +min) {
				min = +current.value;
			}
			data.min = min;
			data.values.push(enrichData(current, i));
			data.years.push(current.year);
		}
		toUnique(data.years);
		return data
	}

	function getAllData() {
		return timeseries[currentFrequency];
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

	function isMonths() {
		return currentFrequency === 'months';
	}

	function isQuarters() {
		return currentFrequency === 'quarters';
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

	/*Chart Controls*/
	function ChartControls() {

		var element = $('[data-chart-controls]');

		function initialize() {
			bindFrequencyChangeButtons();
			bindDisplayChangeButtons();
			bindLinkEvents();
			setCollapsible();
			bindCustomDateFilters();
			setYears();
			resetFilters();
		}

		function changeDates() {
			resolveQuarters();
			resolveMonths();
		}

		function setYears() {
			var years = currentData.years;
			var $fromYear = $('[data-chart-controls-from-year]');
			var $toYear = $('[data-chart-controls-to-year]');
			$fromYear.empty();
			$toYear.empty();
			$.each(years, function(value, key) {
				$fromYear.append($("<option></option>")
					.attr("value", +key).text(key));
				$toYear.append($("<option></option>")
					.attr("value", +key).text(key));
			});
		}

		function resolveQuarters() {
			fromQuarters = $('[data-chart-controls-from-quarter]');
			toQuarters = $('[data-chart-controls-to-quarter]');
			if (isQuarters()) {
				show(fromQuarters);
				show(toQuarters);
			} else {
				hide(fromQuarters);
				hide(toQuarters);
			}

		}

		function resolveMonths() {
			fromMonths = $('[data-chart-controls-from-month]');
			toMonths = $('[data-chart-controls-to-month]');
			if (isMonths()) {
				show(fromMonths);
				show(toMonths);
			} else {
				hide(fromMonths);
				hide(toMonths);
			}
		}

		function resetFilters() {
			/*
			 * Set the select options
			 */
			$('[data-chart-controls-from-month]', element).val(1);
			$('[data-chart-controls-from-quarter]', element).val(1);
			$('[data-chart-controls-from-year]', element).find('option:first-child').attr('selected', true);
			$('[data-chart-controls-to-month]', element).val(12);
			$('[data-chart-controls-to-quarter]', element).val(4);
			$('[data-chart-controls-to-year]', element).find('option:last-child').attr('selected', true);
		}


		/**
		 * Collect the values from the various controls
		 */
		function getFilterValues() {
			return {
				start: {
					year: $('[data-chart-controls-from-year]').val(),
					quarter: $('[data-chart-controls-from-quarter]').val(),
					month: $('[data-chart-controls-from-month]').val()
				},
				end: {
					year: $('[data-chart-controls-to-year]').val(),
					quarter: $('[data-chart-controls-to-quarter]').val(),
					month: $('[data-chart-controls-to-month]').val()
				}
			};
		};

		function getDisplayType() {
			$('[data-chart-controls-from-year]').val()
		}

		function hide(element) {
			element.addClass('js-hidden');
		}

		function show(element) {
			element.removeClass('js-hidden');
		}

		function bindFrequencyChangeButtons() {

			/*
			 * Add click handlers to the controls
			 */

			$('[data-chart-controls-scale]').each(function() {
				var frequency = this.value;
				if (!chart[frequency]) {
					$(this).parent().addClass('btn--secondary--inactive');
				} else {
					if ($(this).data('chart-controls-scale') == currentFrequency) {
						$(this).parent().addClass('btn--secondary--active');
					}
					$(this).on('click', function(e, data) {
						var frequency = this.value;
						toggleSelectedButton();
						changeFrequency(frequency);
					});
				}
			})
		}

		function bindDisplayChangeButtons() {
			$('[data-chart-controls-type]', element).on('click', function(e, data) {
				currentDisplay = $(this).data('chart-controls-type');
				toggleSelectedButton();
				filter();
			});
		}

		function bindCustomDateFilters() {
			$('select', element).change(function() {
				currentFilter = 'custom';
				filter();
			})
		}

		function bindLinkEvents() {

			$('[data-chart-controls-range]', element).on('click', function(e) {

				var elem = $(this);
				var filterDate;
				var fromYear;
				var fromMonth;
				var fromQuarter;
				e.preventDefault();
				toggleSelectedLink(elem);
				var filterValue = elem.data('chart-controls-range');
				if (filterValue === currentFilter) {
					return;
				}
				currentFilter = filterValue;
				resetFilters();


				/*
				 * Work out what the dates are
				 */

				switch (currentFilter) {
					case '10yr':
						filterDate = moment().subtract(10, 'years');
						fromMonth = filterDate.month() + 1;
						fromQuarter = filterDate.quarter() + 1;
						fromYear = filterDate.year();
						break;
					case '5yr':
						filterDate = moment().subtract(5, 'years');
						fromMonth = filterDate.month() + 1;
						fromQuarter = filterDate.quarter() + 1;
						fromYear = filterDate.year();
						break;
					case 'all':
						fromMonth = 1;
						fromQuarter = 1;
						fromYear = $('[data-chart-controls-from-year] option:first-child', element).val();
						break;
				}

				/*
				 * Set the select options
				 */
				$('[data-chart-controls-from-month]', element).find('option[value="' + fromMonth + '"]').attr('selected', true);
				$('[data-chart-controls-from-quarter]', element).find('option[value="' + fromQuarter + '"]').attr('selected', true);
				$('[data-chart-controls-from-year]', element).find('option[value="' + fromYear + '"]').attr('selected', true);

				filter();
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
			initialize: initialize,
			changeDates: changeDates,
			getFilterValues: getFilterValues
		});

	}

	$.extend(this, {})
	return this;

};