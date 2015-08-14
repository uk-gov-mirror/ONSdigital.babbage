package com.github.onsdigital.babbage.request.handler.highcharts.sparkline.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by bren on 14/08/15.
 */
public class Series {


    private List<Entry> series;

    class Entry {
        // Display values:

        public String date;
        public String value;

        // Values split out into explicit components:

        public String year;
        public String month;
        public String quarter;

        /**
         * This field is here so that Rob can see which datasets have contributed
         * values. Please don't rely on it unless and until it has been designed
         * into the app with a genuine purpose.
         */
        public String sourceDataset;

        public Date updateDate;
    }

}
