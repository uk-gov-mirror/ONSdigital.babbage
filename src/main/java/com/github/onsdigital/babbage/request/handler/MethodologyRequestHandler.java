package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class MethodologyRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "methodology";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.article.toString(),PageType.static_qmi.toString()};
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
