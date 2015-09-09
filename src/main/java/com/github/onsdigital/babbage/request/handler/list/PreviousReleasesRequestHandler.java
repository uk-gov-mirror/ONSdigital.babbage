package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.helpers.FilterFields;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;

import java.io.IOException;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends ListPageBaseRequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    @Override
    public String[] getAllowedTypes() {
        return new String[]{ContentType.article.toString(), ContentType.bulletin.toString(), ContentType.compendium_landing_page.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    protected SearchResponseHelper doSearch(SearchRequestHelper searchRequestHelper) throws IOException {
        searchRequestHelper.setSortField(FilterFields.releaseDate.name());
        return super.doSearch(searchRequestHelper);
    }
}
