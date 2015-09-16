package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.request.handler.highcharts.linechart.LineChartImageRequestHandler;
import com.github.onsdigital.babbage.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

/**
 * Created by bren on 30/06/15.
 * Download chart image
 */
@Api
public class ChartImage {

    private final static String PNG = ".png";

    @POST
    /**
     *
     * Generates and servers image as attachment
     */
    public void getChartImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
            String fileName = request.getParameter("fileName");
            fileName = fileName == null ? "image" : fileName;
            fileName += PNG;
            String uri = request.getParameter("uri");
            System.out.println("Download image request recieved" + fileName);
            BabbageResponse babbageResponse = new LineChartImageRequestHandler().get(uri, request);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            babbageResponse.apply(response);
    }

}
