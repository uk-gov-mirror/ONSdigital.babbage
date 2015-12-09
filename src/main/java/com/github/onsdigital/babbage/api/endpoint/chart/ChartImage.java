package com.github.onsdigital.babbage.api.endpoint.chart;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.highcharts.ChartRenderer;
import com.github.onsdigital.babbage.highcharts.HighChartsExportClient;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;

import static com.github.onsdigital.babbage.api.endpoint.chart.ChartRequestUtil.getWidth;

/**
 * Created by bren on 13/10/15.
 */
@Api
public class ChartImage {

    public static final String CONTENT_TYPE = "image/png";

    @GET
    public String get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentReadException {
        String uri = request.getParameter("uri");
        if (StringUtils.isEmpty(uri)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Please specify uri of the chart";
        }
        uri = URIUtil.cleanUri(uri);
        String config = ChartRenderer.getInstance().getChartConfig(uri);
        InputStream stream = HighChartsExportClient.getInstance().getImage(config, getWidth(request));
        new BabbageBinaryResponse(stream, CONTENT_TYPE).apply(request,response);
        return null;
    }
}
