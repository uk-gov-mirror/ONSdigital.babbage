package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class TopicSpecificMethodologyRequestHandler extends ListPageBaseRequestHandler {
    @Override
    public String getRequestType() {
        return "topicspecificmethodology";
    }

    @Override
    public String[] getAllowedTypes() {
        return new String[]{ContentType.static_methodology.toString(),ContentType.static_qmi.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return false;
    }
}
