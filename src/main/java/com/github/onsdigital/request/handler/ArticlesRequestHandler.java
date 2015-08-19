package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class ArticlesRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "articles";
    }

    @Override
    public String getListType() {
        return "article";
    }

    @Override
    public String getTemplateName() {
        return "content/t9-3";
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
