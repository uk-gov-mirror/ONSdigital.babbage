package com.github.onsdigital.configuration;

/**
 * Created by bren on 18/06/15.
 */
public class ConfigurationTest {

    public void testGetSparklineConfig() {
        String sparkline = Configuration.HIGHCHARTS.getSparklineConfig();
//        System.out.println(sparkline);
    }

    public void testGetLinechartConfig() {
        String lineChart = Configuration.HIGHCHARTS.getLinechartConfig();
        System.out.println(lineChart);
    }


}
