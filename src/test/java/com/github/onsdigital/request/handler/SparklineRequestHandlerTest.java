package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.highcharts.SparklineImageRequestHandler;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 18/06/15.
 */
public class SparklineRequestHandlerTest {

    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void testGet() throws Exception {
        SparklineImageRequestHandler handler = new SparklineImageRequestHandler();
        handler.get("/economy/grossdomesticproductgdp/timeseries/ihyq", httpServletRequest);
    }

}
