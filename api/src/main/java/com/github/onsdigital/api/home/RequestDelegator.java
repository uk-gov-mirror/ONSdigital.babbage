package com.github.onsdigital.api.home;

import com.github.onsdigital.api.handler.DataRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Due to url design data, page, charts are all served through the same endpoint.
 * RequestDelegator resolves what type of request is made and delegates flow to appropriate handlers
 */
public class RequestDelegator {

    public static Object handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //TODO: Currently only delegating to data handler. Resolve the actual handler
        handleDataRequest(request, response);
        return null;
    }

    public static void handleDataRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //TODO: Delete data at the end of uri
        new DataRequestHandler().handleDataRequest(request.getRequestURI(), response);
    }

    public static void handlePageRequest(HttpServletRequest request, HttpServletResponse response) {

        return;
    }
}
