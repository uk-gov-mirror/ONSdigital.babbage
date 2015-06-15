package com.github.onsdigital.api.data;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.data.zebedee.ZebedeeUtil;
import com.github.onsdigital.request.handler.DataRequestHandler;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.request.response.BabbageResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.StringReader;

@Api
public class Data {

    @GET
    public Object getData(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {


        try {
            String requestedUri = URIUtil.removeEndpoint(request.getRequestURI());
            ZebedeeRequest zebedeeRequest = ZebedeeUtil.getZebedeeRequest(requestedUri, request.getCookies());
            BabbageResponse getResponse = new DataRequestHandler().get(requestedUri, request, zebedeeRequest);
            response.setCharacterEncoding(getResponse.getCharEncoding());
            response.setContentType(getResponse.getMimeType());
            IOUtils.copy(new StringReader(getResponse.getData()), response.getOutputStream());
        } catch (Exception e) {
            ApiErrorHandler.handle(e, response);
        }
        return null;
    }

}
