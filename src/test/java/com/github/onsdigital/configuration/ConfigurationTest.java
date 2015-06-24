package com.github.onsdigital.configuration;

import org.junit.Test;

/**
 * Created by bren on 18/06/15.
 */
public class ConfigurationTest {

    public void testGetSparklineConfig() {
        String sparkline = Configuration.getSparklineConfig();
//        System.out.println(sparkline);
    }

    public void testGetLinechartConfig() {
        String lineChart = Configuration.getLinechartConfig();
        System.out.println(lineChart);
    }


}
