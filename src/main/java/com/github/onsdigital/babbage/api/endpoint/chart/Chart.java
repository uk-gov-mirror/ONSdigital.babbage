package com.github.onsdigital.babbage.api.endpoint.chart;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.highcharts.ChartRenderer;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

import static com.github.onsdigital.babbage.api.endpoint.chart.ChartRequestUtil.getWidth;

/**
 * Created by bren on 13/10/15.
 * Serves charts as either an image or html depending on jsEnhance cookie on client side
 */
@Api
public class Chart {

    @GET
    public String get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter("uri");
        if (StringUtils.isEmpty(uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Please specify uri of the chart";
        }
        uri = URIUtil.cleanUri(uri);
        String html = ChartRenderer.getInstance().renderChart(uri, getWidth(request));
        new BabbageStringResponse(html,"text/html").apply(response);
        return null;
    }

}
