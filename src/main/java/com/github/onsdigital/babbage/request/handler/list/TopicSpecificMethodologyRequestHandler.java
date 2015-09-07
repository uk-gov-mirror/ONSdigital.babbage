package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.content.page.base.PageType;

/**
 * Render a list page for bulletins under the given URI.
 */
public class TopicSpecificMethodologyRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "topicspecificmethodology";
    }

    @Override
    public String[] getListTypes() {
        return new String[]{PageType.article.toString(),PageType.static_qmi.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
