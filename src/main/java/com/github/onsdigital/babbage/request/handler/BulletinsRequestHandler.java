package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class BulletinsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "bulletins";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.bulletin.toString()};
    }

    @Override
    public String getTemplateName() {
        return "content/t9-4";
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
