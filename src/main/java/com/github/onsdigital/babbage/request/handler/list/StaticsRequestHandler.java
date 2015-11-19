package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;

public class StaticsRequestHandler extends ListPageBaseRequestHandler {

    private final static String REQEUST_TYPE = "staticlist";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.static_page};

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return true;
    }
}
