package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DatasetsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "datasets";
    }

    @Override
    public String getListType() {
        return "dataset";
    }

    @Override
    public String getTemplateName() {
        return "content/t9-5";
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
