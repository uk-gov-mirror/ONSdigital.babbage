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
      options.chart.marginRight = 35;
      if(options.customType === 'line') {
        var tickInterval = options.xAxis.tickInterval || 1;
        if(tickInterval > 1) {
          options.xAxis.tickPositioner = function () {
                         var positions = [];
                         var tick = Math.floor(this.dataMax);
                         for (tick; tick >= this.dataMin; tick -= tickInterval) {
                           positions.push(tick);
                         }
                         return positions;
                       };
        }
      }
      new Highcharts.Chart(options);
    }
  });
}