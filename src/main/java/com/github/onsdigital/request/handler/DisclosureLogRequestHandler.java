package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DisclosureLogRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "disclosurelog";
    }

    @Override
    public String getListType() {
        return "foi";
    }

    @Override
    public String getTemplateName() {
        return "content/t9-2";
    }

    @Override
    public boolean useLocalisedUri() {
        return false;
    }
}
