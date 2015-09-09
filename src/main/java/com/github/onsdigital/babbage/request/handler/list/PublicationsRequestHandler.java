package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublicationsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "publications";
    }

    @Override
    public String[] getAllowedTypes() {
        return new String[]{ContentType.article.toString(),ContentType.bulletin.toString(),ContentType.compendium_landing_page.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
