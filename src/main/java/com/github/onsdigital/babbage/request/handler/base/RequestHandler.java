package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.response.base.BabbageResponse;

import javax.servlet.http.HttpServletRequest;

public interface RequestHandler {
    BabbageResponse get(String uri, HttpServletRequest request) throws Exception;

    /**
     *
     * Should return request type which needs to be handled by this handler.
     *
     * e.g. if request type is data all uris ending with data will be handled by this handler
     * @return
     */
    String getRequestType();

    boolean canHandleRequest(String uri, String requestType);
}
