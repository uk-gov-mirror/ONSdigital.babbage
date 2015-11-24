package com.github.onsdigital.babbage.request.handler.base;

import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.response.BabbageResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by bren on 29/05/15.
 *
 * Classes implementing RequestHandler will automatically be registered to handle urls ending in in given requesttype.
 *
 * See {@link com.github.onsdigital.babbage.request.RequestDelegator} documentation for more info on url design
 *
 */
public interface RequestHandler {
    BabbageResponse get(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException;

    /**
     *
     * Should return request type which needs to be handled by this handler.
     *
     * e.g. if request type is data all uris ending with data will be handled by this handler
     * @return
     */
    String getRequestType();
}
