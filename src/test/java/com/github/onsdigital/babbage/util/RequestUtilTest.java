package com.github.onsdigital.babbage.util;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestUtilTest {

    @Test
    public void getQueryParametersShouldAnEmptyMapWithNoParameters() throws UnsupportedEncodingException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        Map<String,String[]> result = RequestUtil.getQueryParameters(request);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getQueryParametersShouldUrlDecodeParameters() throws UnsupportedEncodingException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getQueryString()).thenReturn("q=economic+review");
        Map<String,String[]> result = RequestUtil.getQueryParameters(request);

        String decoded = result.get("q")[0];
        Assert.assertEquals("economic review", decoded);
    }

    @Test
    public void getLocationShouldReturnSiteDomain() throws Exception {
        TestsUtil.setPrivateStaticField(new RequestUtil(), "SITE_DOMAIN", "MOCK_DOMAIN");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn("other.server");
        when(request.getServerPort()).thenReturn(123);
        Assert.assertEquals("MOCK_DOMAIN:123", RequestUtil.getLocation(request).getHost());
    }
}
