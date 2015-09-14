package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.util.RequestUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * Created by bren on 28/08/15.
 */
@Api
public class Reindex {
    @POST
    public void reIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        String key = request.getParameter("key");
        String uri = request.getParameter("uri");
        try (ContentStream stream = ContentClient.getInstance().reIndex(key, uri )) {
            IOUtils.copy(stream.getDataStream(), response.getOutputStream());
        } catch (ContentReadException ex) {
            HashMap<Object, Object> errorResponse = new HashMap<>();
            response.setStatus(ex.getStatusCode());
            IOUtils.copy(new StringReader(ex.getCause().getMessage()), response.getOutputStream());
        }
    }
}
