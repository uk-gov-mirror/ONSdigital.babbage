package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublicationsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "publications";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.article.toString(),PageType.bulletin.toString(),PageType.compendium_landing_page.toString()};
    }

    @Override
    public String getTemplateName() {
        return "content/t9-8";
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
