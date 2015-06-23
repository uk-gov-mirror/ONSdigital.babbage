{
  "chart" : {
    "backgroundColor" : null,
    "borderWidth" : 0,
    "type" : "area",
    "style" : { "overflow": 'visible' }
  },
  "series" : [ {
    "name" : "",
    "data" : [],
    "marker" : {
      "symbol" : "circle"
    },
    "dashStyle" : "solid"
  } ],
  "title" : {
    "text" : ""
  },
  "subtitle" : {
    "text" : "",
    "y" : 110
  },
  "xAxis" : [ {
    "categories" : [ ],
    "labels" : {
      "formatter" : function(){if (this.isFirst) { return this.value }  if (this.isLast) {  return this.value }},
    "step" : 1
    },
    "tickInterval" : 4.0,
    "tickLength" : 0
    } ],
  "yAxis" : [ {
    "title" : {
      "text" : ""
    },
    "endOnTick" : false,
    "gridLineWidth" : 0,
    "labels" : {
      "enabled" : false
    },
    "startOnTick" : false
  } ],
  "legend" : {
    "enabled" : false
  },
  "exporting" : {
    "enabled" : false
  },
  "credits" : {
    "enabled" : false
  },
  "plotOptions" : {
    "column" : {
      "borderColor" : "#808080"
    },
    "series" : {
      "animation" : false,
      "enableMouseTracking" : false,
      "shadow" : false,
      "states" : {
        "hover" : {
          "lineWidth" : 1
        }
      },
      "lineWidth" : 1
    }
  },
  "tooltip" : {
    "enabled" : false
  },
  "creditOptions" : {
    "enabled" : false
  }
}