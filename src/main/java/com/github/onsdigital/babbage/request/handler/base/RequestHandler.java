package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.response.base.BabbageResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 29/05/15.
 *
 * Classes implementing RequestHandler will automatically be registered to handle urls ending in in given requesttype.
 *
 * See {@link com.github.onsdigital.babbage.request.RequestDelegator} documentation for more info on url design
 *
 */
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
}
