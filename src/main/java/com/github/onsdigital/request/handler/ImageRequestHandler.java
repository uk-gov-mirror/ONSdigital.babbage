package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.request.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.request.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;

public class ImageRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "image";

    public static final String CONTENT_TYPE = "image/png";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageBinaryResponse(null, CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
