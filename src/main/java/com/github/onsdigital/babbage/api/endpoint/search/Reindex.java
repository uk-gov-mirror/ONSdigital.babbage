package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by bren on 28/08/15.
 */
@Api
public class Reindex {
    @POST
    public void reIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        String key = request.getParameter("key");
        String uri = request.getParameter("uri");
        boolean reindexAll = "1".equals(request.getParameter("all"));
        ContentResponse contentResponse = null;
        try {
            if (reindexAll) {
                contentResponse = ContentClient.getInstance().reIndexAll(key);
            } else {
                contentResponse = ContentClient.getInstance().reIndex(key, uri);
            }
            IOUtils.copy(contentResponse.getDataStream(), response.getOutputStream());
        } catch (ContentReadException ex) {
            response.setStatus(ex.getStatusCode());
            IOUtils.copy(new StringReader(ex.getCause().getMessage()), response.getOutputStream());
        }
    }
}
