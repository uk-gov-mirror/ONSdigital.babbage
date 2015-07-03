
function renderChartForUri(uri) {

  var chart;
  var selector = '.markdown-chart'; //'#' + uri.replace(/\//g, '\\/');
  var $graphic = $(selector);
  var pymChild = new pym.Child({});

  function drawGraphic() {
    var chartWidth = $graphic.width(); //- margin.left - margin.right;
    var chartHeight = chartWidth * chart.aspectRatio;
    renderChartObject('.markdown-chart', chart, chartHeight, chartWidth);
    pymChild.sendHeight();
  }

  var dataUri = uri + "/data";
  if (dataUri.indexOf('/') !== 0) {
    dataUri = '/' + dataUri;
  }

  $.ajax({
    url: dataUri,
    type: "GET",
    success: function (data) {
      chart = data;
      drawGraphic();
      window.onresize = drawGraphic;
    }
  });
}


// Do the rendering
function renderChartObject(bindTag, chart, chartHeight, chartWidth) {

  // Create our svg
  var svg = d3.select(bindTag + " svg")
    .attr("viewBox", "0 0 " + chartWidth + " " + chartHeight)
    .attr("preserveAspectRatio", "xMinYMin meet");

   //If we are talking time series skip
  if (chart.isTimeSeries && (chart.chartType == 'line')) {
    renderTimeseriesChartObject(bindTag, chart, chartWidth, chartHeight);
    setFontStyle();
    renderChartUnit();
    return;
  }

  // Calculate padding at top (and left) of SVG
  var types = chart.chartType === 'barline' ? chart.chartTypes : {};
  var groups = chart.chartType === 'barline' ? chart.groups : [];
  var type = checkType(chart);
  var rotate = chart.chartType === 'rotated';
  var yLabel = rotate === true ? chart.unit : '';

  // work out position for chart legend
  var seriesCount = chart.series.length;
  var yOffset = (chart.legend == 'bottom-left' || chart.legend == 'bottom-right') ? seriesCount * 20 + 10 : 5;

  var culledLabels = {};
  var labelRotate = 0;
  var labelInterval = chart.labelInterval;
  _.each(chart.data, function (data_point) {
    if( labelInterval == null ) {
      culledLabels[data_point[chart.headers[0]]] = data_point[chart.headers[0]];
    } else {
      if(labelRotate === 0) {
        culledLabels[data_point[chart.headers[0]]] = data_point[chart.headers[0]];
      } else {
        culledLabels[data_point[chart.headers[0]]] = "";
      }
      labelRotate = (labelRotate + 1) % labelInterval;
    }
  });

  // Generate the chart
  var c3Config = {
    bindto: bindTag,
    size: {
      height: chartHeight,
      width: chartWidth
    },
    data: {
      json: chart.data,
      keys: {
        value: chart.series
      },
      type: type,
      types: types,
      groups: groups,
      colors: getColours(chart.series)
    },
    legend: {
      hide: chart.hideLegend,
      position: 'inset',
      inset: {
        anchor: chart.legend,
        x: 10,
        y: yOffset
      },
      title: chart.title,
      subTitle: chart.subtitle
    },
    axis: {
      x: {
        label: chart.xaxis,
        type: 'category',
        categories: chart.categories,
        tick: {
          format: function (x) {
            var data_point = chart.data[x];
            if(data_point == null) {
              return "";
            } else if( labelInterval == null ) {
              return chart.data[x][chart.headers[0]];
            } else {
              if(x % labelInterval === 0) {
                return chart.data[x][chart.headers[0]];
              } else {
                return "";
              }
            }
          }
        }
      },
      y: {
        label: yLabel,
        position: 'outer-top'
      },
      rotated: rotate
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
             // This line ensures rounding to certain decimal places
               return parseFloat(Math.round(value * Math.pow(10,chart.decimalPlaces)) / Math.pow(10,chart.decimalPlaces)).toFixed(chart.decimalPlaces);
             }
           },
           // This line ensures all data labels are displayed in tooltips when ticks are culled
           title: function (name, ratio, id, index) { return chart.data[name][chart.headers[0]]; }
        }
      }
  };

  c3.generate(c3Config);
  setFontStyle();
  renderChartUnit();

  function setFontStyle() {
    var chartText = d3.select(bindTag + ' svg');
    chartText.style('font-size', '12px')
      .style('font-family', '"Open Sans", sans-serif')
      .style('fill', '#000000');
  }

  function renderChartUnit() {

    var svg = d3.select(bindTag + ' svg');
    var headerGroup = svg.append('g');
    var chartGroup = d3.select('g');

    var transform = chartGroup.attr("transform");
    var chartXOffset = 0;

    if (typeof transform !== 'undefined') {
      var splitParts = transform.split(",");
      chartXOffset = ~~splitParts [0].split("(")[1];
    }
    if (chart.unit && !rotate) {
      headerGroup.append('text') // Unit (if non rotated)
        .attr("transform", "translate(" + (chartXOffset + 10) + "," + 15 + ")")
        .style('font-size', '12px')
        .style('font-family', '"Open Sans", sans-serif')
        .style('fill', '#000000')
        .text(chart.unit);
    }
  }

  function getColours(series) {

    var availableColours = ['#274796','#F5942F','#E73F40','#7BCAE2', '#979796', '#E9E117', '#74B630', '#674796', '#BD5B9E'];
    var colours = {};

    $.each(series, function(index, series) {
      colours[series] = availableColours[index];
    });

    return colours;
  }

  function checkType(chart) {
    if (chart.chartType === 'rotated') {
      type = 'bar';
      return type;
    } else if (chart.chartType === 'barline') {
      type = 'bar';
      return type;
    } else {
      return type = chart.chartType;
    }
  }

  function renderTimeseriesChartObject(bindTag, timechart, chartWidth, chartHeight) {
    var padding = 25;
    var chart = timechart; //timeSubchart(timechart, period);

    // Create a dictionary so we can reverse lookup a tooltip label
    var dates_to_label = {};
    _.each(chart.timeSeries, function (data_point) {
      data_point.date = new Date(data_point.date);
      dates_to_label[data_point.date] = data_point.label;
    });

    // make room for titles if necessary
    if (chart.subtitle != '') {
      padding += 16;
    }
    if (chart.unit != '') {
      padding += 24;
    }

    // should we show
    var showPoints = true;
    if (chart.data.length > 100) {
      showPoints = false;
    }

    // work out position for chart legend
    var seriesCount = chart.series.length;
    var yOffset = (chart.legend == 'bottom-left' || chart.legend == 'bottom-right') ? seriesCount * 20 + 10 : 5;


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
        colors: getColours(chart.series)
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

function renderSvgAnnotations(bindTag, chart, chartHeight, chartWidth) {

  var svg = d3.select(bindTag + ' svg');

  var svgGroups = $(bindTag + ' svg > g').get();
  var headerGroup = svg.append('g');

  //var chartHeight = chartGroup.node().getBBox().height
  // annotate
  var title = headerGroup.append('text') // Title
    .style('font-size', '20px')
    .style('font-family', '"DaxlinePro", sans-serif')
    .style('fill', '#000000')
    .text(chart.title);

  var currentYOffset = 8 + applyLineWrap(title, chartWidth);

  if (chart.subtitle != '') {
    var subtitle = headerGroup.append('text') // Subtitle
      .attr("transform", "translate(0," + currentYOffset + ")")
      .style('font-size', '15px')
      .style('font-family', '"Open Sans", sans-serif')
      .style('fill', '#999999')
      .text(chart.subtitle);

    currentYOffset += 8 + applyLineWrap(subtitle, chartWidth);
  }

  currentYOffset += 2;

  // offset all the existing top level groups. This includes the chart and the legend
  var arrayLength = svgGroups.length;
  for (var i = 0; i < arrayLength; i++) { // ignore the last group as we just added it
    var group = svgGroups[i];


    var transform = $(group).attr("transform");
    var xOffset = 0;
    var yOffset = 0;

    if (typeof transform !== 'undefined') {
      var splitParts = transform.split(",");
      xOffset = ~~splitParts [0].split("(")[1];
      yOffset = ~~splitParts [1].split("(")[1];
    }

    $(group).attr("transform", "translate(" + (xOffset) + "," + (currentYOffset + yOffset) + ")");
  }

  currentYOffset += chartHeight;

  if (chart.source != '') {
    var source = d3.select(bindTag + ' svg').append('text') // Source
      .attr("transform", "translate(" + chartWidth + "," + currentYOffset + ")")
      .attr('text-anchor', 'end')
      .style('font-size', '12px')
      .style('font-family', '"Open Sans", sans-serif')
      .style('fill', '#999999')
      .text(chart.source);

    currentYOffset += 5 + applyLineWrap(source, chartWidth);
  }

  // reset the max height property of the container div.
  // C3 seems to set this and it becomes a stale value after rendering annotations.
  $(bindTag + ' svg').attr('height', currentYOffset);
  $(bindTag).css('max-height', currentYOffset +'px');

  return currentYOffset;


  // apply word wrap if required on text we have inserted
  function applyLineWrap(text, width) {

    var wrappedHeight = 0;

    text.each(function() {
      var text = d3.select(this),
        words = text.text().split(/\s+/).reverse(),
        word,
        line = [],
        lineNumber = 0,
        lineHeight = 1.2,
        y = text.attr("y"),
        tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", lineHeight + "em");
      while (word = words.pop()) {
        line.push(word);
        tspan.text(line.join(" "));
        if (tspan.node().getComputedTextLength() > width) {
          line.pop();
          tspan.text(line.join(" "));
          line = [word];
          tspan = text.append("tspan").attr("x", 0).attr("y", y).attr("y", ((++lineNumber + 1) * lineHeight) + "em").text(word);
        }
      }

      wrappedHeight = tspan.node().getBBox().height;
    });

    return wrappedHeight;
  }
}