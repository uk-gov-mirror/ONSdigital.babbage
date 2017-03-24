package com.github.onsdigital.babbage.search.model;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by guidof on 22/03/17.
 */
public class TimeSeriesResultFactoryTest {

    private TimeSeriesResult timeSeriesResult;

    @Before
    public void init() throws IOException {
        final File file = new File(
                "src/test/resources/com/github/onsdigital/babbage/search/model/TimeSeriesResultsFactorySample.json");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        timeSeriesResult = TimeSeriesResultFactory.getInstance(bytes);
    }

    @Test
    public void testSuccessfulResponse() {
        assertNotNull(timeSeriesResult);
        assertEquals("/economy/nationalaccounts/uksectoraccounts/timeseries/ml4d/capstk", timeSeriesResult.getId());
        assertEquals("/economy/nationalaccounts/uksectoraccounts/timeseries/ml4d/capstk", timeSeriesResult.getUri());
        assertEquals("timeseries", timeSeriesResult.getType());
    }
}