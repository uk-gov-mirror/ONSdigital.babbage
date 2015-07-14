function getLinechartConfig(timeseries) {
 return  {
   /*Do not delete or change chart:start chart:end comment blocks as it is used as markers for chart config for server side image rendering*/
   /*chart:start*/
   chart: {
     type: 'line'
   },
   colors: ['#007dc3', '#409ed2', '#7fbee1', '#007dc3', '#409ed2', '#7fbee1'],

   title: {
     text: timeseries.description.title
   },
   subtitle: {
     text: 'Source: ' + timeseries.description.source,
     floating: true,
     align: 'right',
     x: 0,
     verticalAlign: 'bottom',
     y: 10
   },
   navigation: {
     buttonOptions: {
       enabled: false
     }
   },
   xAxis: {
     categories: [],
     tickInterval: ':tickInterval:',
     labels: {
       formatter: function() {
         var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
         var response = '';
         if (w < 768) {
           if (this.isFirst) {
             count = 0;
           }
           if (count % 3 === 0) {
             response = this.value;
           }
           count++;
         } else {
           response = this.value;
         }
         return response;
       }
     },
     tickmarkPlacement: 'on'
   },
   yAxis: {
     title: {
       text: timeseries.description.preUnit + ' ' + timeseries.description.unit,
       align: 'high',
       rotation: 0,
       textAlign: 'left',
       x: 10,
       y: -10
     },
     min: ':yMin:'

   },

   credits: {
     enabled: false
   },

   plotOptions: {
     series: {
       shadow: false,
       states: {
         hover: {
           enabled: true,
           shadow: false,
           lineWidth: 3,
           lineWidthPlus: 0,
           marker: {
             height: 0,
             width: 0,
             halo: false,
             enabled: true,
             fillColor: null,
             radiusPlus: null,
             lineWidth: 3,
             lineWidthPlus: 0
           }
         }
       }
     }
   },
   tooltip: {
     shared: true,
     width: '150px',
     crosshairs: {
       width: 2,
       color: '#f37121'
     },
     positioner: function(labelWidth, labelHeight, point) {
       var w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
       var points = {
         x: 30,
         y: 42
       };
       var tooltipX, tooltipY;
       var chart = Highcharts.charts[Highcharts.charts.length - 1];
       if (w > 768) {

         if (point.plotX + labelWidth > chart.plotWidth) {
           tooltipX = point.plotX + chart.plotLeft - labelWidth - 20;
           $("#custom-tooltip").removeClass('tooltip-left');
         } else {
           tooltipX = point.plotX + chart.plotLeft + 20;
           $("#custom-tooltip").removeClass('tooltip-right');
         }

         tooltipY = 50;
         points = {
           x: tooltipX,
           y: tooltipY
         };
       } else {
         $("#custom-tooltip").removeClass('tooltip-left');
         $("#custom-tooltip").removeClass('tooltip-right');
       }

       return points;
     },

     formatter: function() {
       var id1 = '<div id="custom-tooltip" class="tooltip-left tooltip-right">';
       var block = id1 + "<div class='sidebar' >";
       var title = '<b class="title">' + this.points[0].key + ': </b>';
       var symbol = ['<div class="circle">●</div>', '<div class="square">■</div>', '<div class="diamond">♦</div>', '<div class="triangle">▲</div>', '<div class="triangle">▼</div>'];

       var content = block;

       // symbols
       $.each(this.points, function(i, val) {
         content += symbol[i];
       });

       content += "<br/></div>";
       content += "<div class='maintext'>";
       content += title;

       // series names and values
       $.each(this.points, function(i, val) {
         content += '<div class="tiptext">' + val.point.series.chart.series[i].name + "<br/><b>Value: " + val.point.series.chart.series[i].options.preUnit + val.y + " " + val.point.series.chart.series[i].options.unit + '</b></div>';
       });
       content += "</div>";
       return content;
     },

     backgroundColor: 'rgba(255, 255, 255, 0)',
     borderWidth: 0,
     borderColor: 'rgba(255, 255, 255, 0)',
     shadow: false,
     useHTML: true

   },
   series: [{
     name: timeseries.description.title,
     id: timeseries.description.cdid,
     unit: timeseries.description.unit,
     preUnit: timeseries.description.preUnit,
     data: ':data:',
     marker: {
       enabled:false,
       symbol: "circle",
       states: {
         hover: {
           fillColor: '#007dc3',
           radiusPlus: 0,
           lineWidthPlus: 0
         }
       }
     },
     dashStyle: 'Solid'
   }]
   /*chart:end*/
 };
 }
