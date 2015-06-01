package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.RequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "/";

    @Override
    public Object handle(String requestedUri, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");
        return null;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
