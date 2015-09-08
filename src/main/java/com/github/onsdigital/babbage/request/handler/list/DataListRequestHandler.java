package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

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
        return new String[]{PageType.dataset.toString(), PageType.timeseries.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
