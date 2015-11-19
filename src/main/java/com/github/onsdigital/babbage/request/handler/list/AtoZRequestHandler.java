package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Created by bren on 19/11/15.
 */
public class AtoZRequestHandler extends ListPageBaseRequestHandler {

    private final static String REQUEST_TYPE = "atoz";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.bulletin};

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}
