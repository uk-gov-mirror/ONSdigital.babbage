package com.github.onsdigital.request.handler;

import com.github.onsdigital.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class BulletinsRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "bulletins";
    }

    @Override
    public String getListType() {
        return "bulletin";
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
