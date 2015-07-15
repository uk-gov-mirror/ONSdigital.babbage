function getMarkdownChartConfig(chart) {
 return  {
   /*Do not delete or change chart:start chart:end comment blocks as it is used as markers for chart config for server side image rendering*/
   /*chart:start*/
   chart: {
     renderTo: chart.uri,
     height: ':chartHeight:',
     width: ':chartWidth:'
     //marginTop: marginTop
   },
   colors: ['#274796','#F5942F','#E73F40','#7BCAE2', '#979796', '#E9E117', '#74B630', '#674796', '#BD5B9E'],
   title:{
     text:''
   },
   xAxis: {
     categories: chart.categories,
     tickInterval: chart.labelInterval
   },
   //yAxis: yAxis,
   series: ':series:',
   tooltip: {
     valueDecimals:chart.decimalPlaces
   },
   plotOptions: {
     series: {
       animation: false
     }
   },
   credits: {
     enabled: false
   }
   /*chart:end*/
 };
 }
