package com.github.onsdigital.babbage.request;

import com.github.onsdigital.babbage.request.handler.PageRequestHandler;
import com.github.onsdigital.babbage.request.handler.TimeseriesLandingRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.handler.content.DataRequestHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by bren on 01/06/15.
 */

public class RequestDelegatorTest {

    @Test
    public void testResolveRequestHandler() {
        RequestHandler handler = RequestDelegator.resolveRequestHandler("data");
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler instanceof DataRequestHandler);

        handler = RequestDelegator.resolveRequestHandler("/");
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler instanceof PageRequestHandler);

        handler = RequestDelegator.resolveRequestHandler("/randomuri");
        Assert.assertNull(handler);
    }

    @Test
    public void timeseriesLandingPageUriShouldReturnTimeseriesLandingHandler() {

        // Given a URI of a timeseries landing page
        RequestHandler handler = RequestDelegator.resolveRequestHandler("/some/uri/timeseries/abcd");
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler instanceof TimeseriesLandingRequestHandler);

        // or /data uri of a timeseries landing page
        handler = RequestDelegator.resolveRequestHandler("/some/uri/timeseries/abcd/data");
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler instanceof TimeseriesLandingRequestHandler);
    }
}
