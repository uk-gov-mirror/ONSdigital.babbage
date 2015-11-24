package com.github.onsdigital.babbage.api.endpoint.search.base;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.json.JsonPageRequestHandler;
import com.github.onsdigital.babbage.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

import static com.github.onsdigital.babbage.api.error.ErrorHandler.handle;
import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static com.github.onsdigital.babbage.util.URIUtil.removeLastSegment;

/**
 * Created by bren on 24/11/15.
 *
 * Base search page functionality
 */
@Api
public abstract class BaseSearchPage extends ListPageBaseRequestHandler implements JsonPageRequestHandler {

    /**
     * Resolves if request is a page request or a json request and calls appropriate method
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        try {
            if (RequestUtil.isJsonRequest(request)) {
                getData(cleanUri(uri), request).apply(response);
            } else {
                getPage(removeLastSegment(uri), request).apply(response);
            }
        } catch (IOException e) {
            handle(request, response, e);
        }
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }

    @Override
    protected boolean isListTopics() {
        return false;
    }
}
