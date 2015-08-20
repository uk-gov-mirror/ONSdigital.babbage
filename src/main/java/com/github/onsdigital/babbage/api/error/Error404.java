package com.github.onsdigital.babbage.api.error;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.framework.NotFound;
import com.github.onsdigital.babbage.request.RequestDelegator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;


/**
 * Error404 is used as a delegator rather than a not found handler due to url design with request type being at the end of urls for data and other endpoints
 *
 */
@Api
public class Error404 implements NotFound {

    @GET
    public void demo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        handle(req, res);
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RequestDelegator.get(request, response);
        } catch (Throwable e) {
            new ErrorHandler().handle(request, response, null, e);
        }
        return null;
    }

}