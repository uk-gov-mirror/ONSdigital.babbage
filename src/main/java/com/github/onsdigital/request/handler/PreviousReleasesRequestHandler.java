package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageStringResponse("", CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
