package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by bren on 01/07/15.
 * <p/>
 * Serves resource files from content service with no content disposition
 */
@Api
public class Resource {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        try {
            ContentResponse contentResponse = ContentClient.getInstance().getResource(request.getParameter("uri"));
            String contentDispositionHeader = "inline; ";
            contentDispositionHeader += contentResponse.getName() == null ? "" : "filename=\"" + contentResponse.getName() + "\"";
            response.setHeader("Content-Disposition", contentDispositionHeader);
            new BabbageContentBasedBinaryResponse(contentResponse, contentResponse.getDataStream(), contentResponse.getMimeType()).apply(request, response);
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
        }
    }
}
