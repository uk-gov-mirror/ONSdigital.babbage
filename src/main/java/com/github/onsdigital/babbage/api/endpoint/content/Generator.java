package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Map;

/**
 * Proxy requests to zebedee generator endpoint.
 */
@Api
public class Generator {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

        Map<String, String[]> queryParameters = RequestUtil.getQueryParameters(request);

        try (ContentStream contentStream = ContentClient.getInstance().getGenerator(request.getParameter("uri"), queryParameters)) {
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
