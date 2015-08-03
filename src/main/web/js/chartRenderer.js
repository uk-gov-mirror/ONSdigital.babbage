function renderChartForUri(uri, id) {

  var dataUri = uri + "/markdownchartconfig";
  if (dataUri.indexOf('/') !== 0) {
    dataUri = '/' + dataUri;
  }

  $.ajax({
    url: dataUri,
    type: "GET",
    success: function (options) {
      options.chart.renderTo = id;
      new Highcharts.Chart(options);
    }
  });
}

//function renderChartForUri(uri, id, $graphic) {
//
//  var chart;
//
//  function drawGraphic() {
//    var chartWidth = $graphic.width();
//    var chartHeight = chartWidth * chart.aspectRatio;
//    renderChartObject(id, chart, chartHeight, chartWidth);
//  }
//
//  var dataUri = uri + "/data";
//  if (dataUri.indexOf('/') !== 0) {
//    dataUri = '/' + dataUri;
//  }
//
//  $.ajax({
//    url: dataUri,
//    type: "GET",
//    success: function (data) {
//      chart = data;
//      drawGraphic();
//    }
//  });
//}
//
//// Do the rendering
function renderChartObject(bindTag, chart, chartHeight, chartWidth) {

  var chartType = checkType(chart.chartType);
  var stacked = false;

  var series = [];
  $.each(chart.series, function(i, seriesName) {

    var seriesType = chartType;
    if (chart.chartType === 'barline') {
      seriesType = checkType(chart.chartTypes[seriesName]);
    }

    var data = [];
    $.each(chart.data, function (j, seriesData) {
      var value = parseFloat(seriesData[seriesName]);
      if(isNaN(value)) {
        value = null;
      }

      //if(chart.isTimeSeries) { // type = line?
      //  var date = new Date(seriesData['date']);
      //  //data.push([Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDay()),value])
      //  data.push([Date.UTC(date.getFullYear(), date.getMonth()),value])
      //} else {
        data.push(value)
      //}
    });

    var seriesItem = {
      name: seriesName,
      data: data,
      type: seriesType
    };

    if (chart.chartType === 'barline') {
      if ($.inArray(seriesName, chart.groups[0]) > -1) {
        seriesItem.stack = 'group1'; // we only support one group.
        stacked = true;
      } else {
        seriesItem.stack = seriesName; // set a unique stack group to not stack
      }
    }

    series.push(seriesItem);
  });

  //var marginTop = 35; // todo: if type = bar set to 0
  var yAxis = {
    title: {
      text: chart.unit,
      align: "high"
    }
  };
  var labels = {};

  // typically do not use the chart label on y axis, just overlay a label with the unit inside the chart area.
  if (chart.chartType !== 'rotated') {
    yAxis = {
      title: {
        text: ''
      },
      lineWidth: 1
    };

    labels = {
      items: [
        {
          html: chart.unit,
          style: {
            left: '0px',
            top: '0px'
          }
        }
      ]
    }
  }

  var xAxis = {
    categories: chart.categories,
    tickInterval: chart.labelInterval
  };

  //if(chart.isTimeSeries) {
  //  xAxis = {
  //    type: 'datetime'
  //  }
  //}

  // render chart
  var options = {
    chart: {
      renderTo: bindTag,
      height: chartHeight,
      width: chartWidth
    },
    colors: ['#274796','#F5942F','#E73F40','#7BCAE2', '#979796', '#E9E117', '#74B630', '#674796', '#BD5B9E'],
    title:{
      text:''
    },
    labels: labels,
    xAxis: xAxis,
    yAxis: yAxis,
    series: series,
    plotOptions: {
      series: {
        animation: false,
        pointPadding: 0,
        groupPadding: 0.1
      },
      line: {
        lineWidth:1,
        marker: {
          radius: 2,
          symbol: 'circle'
        }
      }
    },
    legend: {
      verticalAlign: "top",
      enabled : (series.length > 1)
    },
    tooltip: {
      valueDecimals:chart.decimalPlaces,
      shared: true
    },
    credits: {
      enabled: false
    }
  };

  if (stacked) {
    options.plotOptions.column= {
        stacking: 'normal'
      }
  }

  var chart = new Highcharts.Chart(options);

  function checkType(chartType) {

    if (chartType === 'rotated') {
      type = 'bar';
      return type;
    } else if (chartType === 'barline') {
      type = 'column';
      return type;
    } else if (chartType === 'bar') {
        type = 'column';
        return type;
    } else {
      return type = chartType;
    }


    //"stackedArea">Stacked Area</option>
    //<option value="stackedPercent">Stacked Percent</option>
    //<option value="pyramid">Pyram
  }

  function renderTimeseriesChartObject(bindTag, timechart, chartWidth, chartHeight) {
    var chart = timechart; //timeSubchart(timechart, period);

    // Create a dictionary so we can reverse lookup a tooltip label
    //var dates_to_label = {};
    //_.each(chart.timeSeries, function (data_point) {
    //  data_point.date = new Date(data_point.date);
    //  dates_to_label[data_point.date] = data_point.label;
    //});

    // should we show
    var showPoints = true;
    if (chart.data.length > 100) {
      showPoints = false;
    }

    // refers to the issue of time axes not being applicable to non continuous charts
    var axisType;
    var keys;

    if (chart.chartType == 'line') { // continuous line charts
      axisType = {
        label: chart.xaxis,
        type: 'timeseries',
      }

      var monthsOnTimeline = (chart.timeSeries[chart.timeSeries.length - 1].date - chart.timeSeries[0].date) / (1000 * 60 * 60 * 24 * 30);
      var tick = {
        format: function (x) {
          return x.getFullYear();
        }
      }
      if (monthsOnTimeline <= 24.5) {
        tick = {
          format: function (x) {
            return formattedMonthYear(x);
          }
        }
      }


      axisType.tick = tick;
      keys = {
        x: 'date',
        value: chart.series
      }
    } else { // bar charts and other
      axisType = {
        label: chart.xaxis,
        type: 'category',
        categories: chart.categories
      }
      keys = {
        x: 'label',
        value: chart.series
      }
    }

    c3.generate({
      bindto: bindTag,
      size: {
        height: chartHeight,
        width: chartWidth
      },
      padding: {
        right: 15
      },
      data: {
        json: chart.timeSeries,
        keys: keys,
        type: chart.chartType,
        xFormat: '%Y-%m-%d %H:%M:%S',
        //colors: getColours(chart.series)
      },

      point: {
        show: showPoints
      },

      legend: {
        hide: chart.hideLegend,
        position: 'inset',
        inset: {
          anchor: chart.legend,
          x: 10,
          y: yOffset
        }
      },

      axis: {
        x: axisType
      },
      tooltip: {
        format: {
          title: function (x) {
            return dates_to_label[x];
          }
        }
      },
      grid: {
        y: {
          show: true
        }
      },
       tooltip: {
         format: {
           value: function (value, ratio, id, index) {
              if(chart.decimalPlaces == null) {
                return value;
              } else {
                return parseFloat(Math.round(value * Math.pow(10,chart.decimalPlaces)) / Math.pow(10,chart.decimalPlaces)).toFixed(chart.decimalPlaces);
              }
            }
         }
       }
    });
  }

  function formattedMonthYear(date) {
    var monthNames = [
      "Jan", "Feb", "Mar",
      "Apr", "May", "Jun", "Jul",
      "Aug", "Sep", "Oct",
      "Nov", "Dec"];

    var monthIndex = date.getMonth();
    var year = date.getFullYear();

    return monthNames[monthIndex] + " " + year;
  }
}
