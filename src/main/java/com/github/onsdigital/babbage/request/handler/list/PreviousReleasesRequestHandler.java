package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SortBy;

import java.io.IOException;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends ListPageBaseRequestHandler {

    private static final String REQEUST_TYPE = "previousreleases";
    private final static String[] ALLOWED_TYPES = new String[]{ContentType.article.toString(), ContentType.bulletin.toString(), ContentType.compendium_landing_page.toString()};

    @Override
    public String[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    protected SearchResponseHelper doSearch(SearchRequestHelper searchRequestHelper) throws IOException {
        searchRequestHelper.setSortBy(SortBy.RELEASE_DATE);
        return super.doSearch(searchRequestHelper);
    }
}
