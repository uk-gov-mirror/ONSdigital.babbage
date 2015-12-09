package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by bren on 30/11/15.
 * <p/>
 * Proxy requests to zebedee export endpoint
 */
@Api
public class Export {

    @POST
    public void post(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

        try (ContentStream contentStream = ContentClient.getInstance().export(request.getParameter("format"), request.getParameterValues("uri"))) {
            String contentDispositionHeader = "attachment; ";
            contentDispositionHeader += contentStream.getName() == null ? "" : "filename=\"" + contentStream.getName() + "\"";
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", contentDispositionHeader);
            new BabbageBinaryResponse(contentStream.getDataStream(), contentStream.getMimeType()).apply(request,response);
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
        }
    }
}
