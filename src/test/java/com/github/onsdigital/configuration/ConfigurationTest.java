package com.github.onsdigital.configuration;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by bren on 18/06/15.
 */
public class ConfigurationTest {


    @Test
    public void testGetSparklineConfig() {
        String sparkline = Configuration.getSparklineConfig();
        System.out.println(sparkline);
        Assert.assertTrue(StringUtils.isNotEmpty(sparkline));
    }

}
