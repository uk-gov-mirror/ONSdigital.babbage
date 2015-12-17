package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class TopicSpecificMethodologyRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.static_methodology, ContentType.static_methodology_download, ContentType.static_qmi};
    private final static String REQUEST_TYPE = "topicspecificmethodology";

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return true;
    }

    @Override
    protected boolean isAggregateByType() {
        return true;
    }
}
