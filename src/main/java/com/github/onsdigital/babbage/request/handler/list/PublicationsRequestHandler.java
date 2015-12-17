package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublicationsRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.article, ContentType.article_download, ContentType.bulletin, ContentType.compendium_landing_page};
    private final static String REQUEST_TYPE = "publications";

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    protected boolean isFilterLatest(HttpServletRequest request) {
        //filter if not wanted to be included specifically
        return request.getParameter("allReleases") == null;
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
