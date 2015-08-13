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
			marginRight: 100,
			marginLeft: 100,
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
				crop: false,
				style: {
					'font-size': '35px',
					'color': '#707070'
				},
				formatter: function() {
					if (this.isFirst) {
						return this.value;
					}
					if (this.isLast) {
						return this.value;
					}
				},
				step: 1
			},
			tickLength: 0,
			//tickInterval: ':tickInterval:',
			lineColor: '#707070',
      lineWidth: 2
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
				lineWidth: 2,
				shadow: false,
				states: {
					hover: {
						lineWidth: 1
					}
				},
				marker: {
					radius: 3,
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
			color: '#007dc3',
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
