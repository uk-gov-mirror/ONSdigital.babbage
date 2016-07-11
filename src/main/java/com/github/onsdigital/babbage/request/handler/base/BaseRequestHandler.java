package com.github.onsdigital.babbage.request.handler.base;

/**
 * Created by bren on 29/05/15.
 *
 * Classes implementing RequestHandler will automatically be registered to handle urls ending in in given requesttype.
 *
 * See {@link com.github.onsdigital.babbage.request.RequestDelegator} documentation for more info on url design
 *
 */
public abstract class BaseRequestHandler implements RequestHandler {

    /**
     * return true if this request handler can handler the request.
     * This has a default implementation as most handlers are based on the request type and do a simple string
     * comparison. You can override this if you need to do something more complex for a particular handler.
     * @param uri - the uri of the request
     * @param requestType - the request type (last segment of the uri)
     * @return
     */
    @Override
    public boolean canHandleRequest(String uri, String requestType) {
        return getRequestType().equals(requestType);
    }
}
