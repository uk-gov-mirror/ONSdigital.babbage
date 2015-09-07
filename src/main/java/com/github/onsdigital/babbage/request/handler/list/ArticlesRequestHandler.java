package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class ArticlesRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "articles";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.article.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
