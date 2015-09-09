package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DataListRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "datalist";
    }

    @Override
    public String[] getAllowedTypes() {
        return new String[]{ContentType.dataset.toString(), ContentType.timeseries.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
