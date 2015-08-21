package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DatasetsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "datasets";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.dataset.toString()};
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
