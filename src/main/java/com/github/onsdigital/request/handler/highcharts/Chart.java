package com.github.onsdigital.request.handler.highcharts;

import java.net.URI;
import java.util.Map;

public class Chart {
    private String type;
    private String chartType;
    private double aspectRatio;
    private URI uri;
    private Map<String, String>[] data;
    private int decimalPlaces;
    private int labelInterval;
}

//{
//        "type": "chart",
//        "title": "Line",
//        "filename": "61f3c0e8",
//        "uri": "/businessindustryandtrade/businessactivitysizeandlocation/bulletins/werwrw/2015-07-30/61f3c0e8",
//        "subtitle": "",
//        "unit": "",
//        "source": "",
//        "decimalPlaces": "2",
//        "labelInterval": "",
//        "notes": "",
//        "altText": "",
//        "data": [
//        {
//        "date": "2000-01-01",
//        "Juice": "106.3",
//        "Travel": "49.843099"
//        },
//        {
//        "date": "2000-02-01",
//        "Juice": "106.0",
//        "Travel": "49.931931"
//        },
//        ],
//        "headers": [
//        "date",
//        "Juice",
//        "Travel"
//        ],
//        "series": [
//        "Juice",
//        "Travel"
//        ],
//        "categories": [
//        "2000-01-01",
//        "2000-02-01",
//        "2000-03-01",
//        "2000-04-01",
//        "2000-05-01",
//        "2000-06-01",
//        "2000-07-01"
//        ],
//        "aspectRatio": "0.75",
//        "chartType": "barline",
//        "files": [
//        {
//        "type": "download-png",
//        "filename": "61f3c0e8-download.png"
//        },
//        {
//        "type": "png",
//        "filename": "61f3c0e8.png"
//        }
//        ],
//        "chartTypes": {
//        "Juice": "bar",
//        "Travel": "bar"
//        },
//        "groups": [
//        [
//        "Juice",
//        "Travel"
//        ]
//        ]
//        }