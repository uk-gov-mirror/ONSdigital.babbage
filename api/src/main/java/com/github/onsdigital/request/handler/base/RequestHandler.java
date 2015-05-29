package com.github.onsdigital.request.handler.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 29/05/15.
 *
 * Classes implementing RequestHandler will automatically be registered to handle urls ending in with that class name.
 *
 * See {@link com.github.onsdigital.request.RequestDelegator} documentation for more info on url design
 *
 */
public interface RequestHandler {
    Object handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     *
     * Should return request type which needs to be handled by this handler.
     *
     * e.g. if request type is data all uris ending with data will be handled by this handler
     * @return
     */
    String getRequestType();
}
