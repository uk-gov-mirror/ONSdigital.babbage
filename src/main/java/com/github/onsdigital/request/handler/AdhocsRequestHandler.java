package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class AdhocsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "adhocs";
    }

    @Override
    public String getListType() {
        return "adhoc";
    }

    @Override
    public String getTemplateName() {
        return "content/t9-1";
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
