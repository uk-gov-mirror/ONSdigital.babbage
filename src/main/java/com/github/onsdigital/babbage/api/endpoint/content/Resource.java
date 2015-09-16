package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by bren on 01/07/15.
 * <p>
 * Serves resource files from content service with no content disposition
 */
@Api
public class Resource {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentNotFoundException {
        try (ContentStream contentStream = ContentClient.getInstance().getResource(request.getParameter("uri"));) {
            String contentDispositionHeader = "inline; ";
            contentDispositionHeader += contentStream.getName() == null ? "" : "filename=\"" + contentStream.getName() + "\"";
            response.setHeader("Content-Disposition", contentDispositionHeader);
            new BabbageBinaryResponse(contentStream.getDataStream(), contentStream.getMimeType()).applyData(response);
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
        }
    }
}
