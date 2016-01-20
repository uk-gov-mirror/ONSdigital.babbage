package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

/**
 * Created by bren on 25/11/15.
 */
@Api
public class TimeSeriesTool extends ListPageBaseRequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.timeseries};

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = URIUtil.cleanUri(request.getRequestURI());
        String requestType = URIUtil.resolveRequestType(uri);
        if ("data".equals(requestType)) {
            getData(URIUtil.removeLastSegment(uri), request).apply(request, response);
        } else {
            super.get(uri, request).apply(request,response);
        }
    }

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}
