function getSparklineConfig(timeseries) {
	return {
		/*Do not delete or change chart:start chart:end comment blocks as it is used as markers for chart config for server side image rendering*/
		/*chart:start*/
		chart: {
			backgroundColor: null,
			borderWidth: 0,
			type: 'area',
			style: {
				overflow: 'visible'
			},
			skipClone: true
		},
		title: {
			text: ''
		},
		subtitle: {
			text: '',
			y: 110
		},
		credits: {
			enabled: false
		},
		xAxis: {
			categories: [],
			labels: {
				style: {
					'font-size': '30px'
				},
				formatter: function() {
					if (this.isFirst) {
						return this.value
					}
					if (this.isLast) {
						return this.value
					}
				},
				step: 1
			},
			tickLength: 0,
			tickInterval: ':tickInterval:',
			lineColor: 'transparent'
		},
		yAxis: {
			endOnTick: false,
			startOnTick: false,
			labels: {
				enabled: false
			},
			title: {
				text: null
			},
			tickPositions: [0]
		},
		legend: {
			enabled: false
		},
		tooltip: {
			enabled: false
		},
		plotOptions: {
			series: {
				animation: false,
				lineWidth: 1,
				shadow: false,
				states: {
					hover: {
						lineWidth: 1
					}
				},
				marker: {
					radius: 1,
					states: {
						hover: {
							radius: 2
						}
					}
				},
				fillOpacity: 0.25,
				enableMouseTracking: false,
			},
			column: {
				negativeColor: '#910000',
				borderColor: 'silver'
			}
		},
		exporting: {
			enabled: false
		},
		series: [{
			name: '',
			data: ':data:',
			marker: {
				symbol: 'circle',
				states: {
					hover: {
						fillColor: '#007dc3',
						radiusPlus: 0,
						lineWidthPlus: 0
					}
				}
			},
			dashStyle: 'Solid',
		}]

		/*chart:end*/
	}
}