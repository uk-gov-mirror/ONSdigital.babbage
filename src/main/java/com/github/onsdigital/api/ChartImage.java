package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.request.handler.highcharts.LineChartImageHandler;
import com.github.onsdigital.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;

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
    public void getChartImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        try {
            String fileName = request.getParameter("fileName");
            fileName = fileName == null ? "image" : fileName;
            fileName += PNG;
            String uri = request.getParameter("uri");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            System.out.println("Download image request recieved" + fileName);
            //TODO:Read chart data from zebedee ? Think about ability to proxy every request to zebedee.
            //TODO: Lots of common flow in Babbage and Zebedee
            BabbageResponse babbageResponse = new LineChartImageHandler().get(uri, request);
            babbageResponse.apply(response);
        } catch (Exception e) {
            ApiErrorHandler.handle(e,response);
        }
    }

}
