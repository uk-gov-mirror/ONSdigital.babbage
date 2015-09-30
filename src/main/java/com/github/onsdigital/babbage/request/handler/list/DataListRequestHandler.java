package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DataListRequestHandler extends ListPageBaseRequestHandler {

    private final static String REQEUST_TYPE = "datalist";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.dataset, ContentType.reference_tables, ContentType.timeseries_dataset, ContentType.timeseries};

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() { return ALLOWED_TYPES; } @Override public boolean isLocalisedUri() { return true; } }
