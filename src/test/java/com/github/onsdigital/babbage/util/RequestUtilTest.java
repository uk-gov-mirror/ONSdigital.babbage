package com.github.onsdigital.babbage.util;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
}
