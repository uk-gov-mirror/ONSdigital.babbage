package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublishedRequestsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "publishedrequests";
    }

    @Override
    public String[] getAllowedTypes() {
        return new String[]{ContentType.static_foi.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return false;
    }
}
