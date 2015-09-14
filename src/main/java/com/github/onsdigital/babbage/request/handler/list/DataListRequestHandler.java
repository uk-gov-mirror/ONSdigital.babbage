package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DataListRequestHandler extends ListPageBaseRequestHandler {

    private final static String REQEUST_TYPE = "datalist";
    private final static String[] ALLOWED_TYPES = new String[]{ContentType.dataset.toString(), ContentType.timeseries.toString()};

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public String[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
