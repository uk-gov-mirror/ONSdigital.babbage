package com.github.onsdigital.babbage.api.endpoint.chart;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.highcharts.ChartRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by bren on 13/10/15.
 */
@Api
public class ChartImage {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentReadException {
        ChartRenderer.getInstance().renderChartImage(request, response);
    }
}
