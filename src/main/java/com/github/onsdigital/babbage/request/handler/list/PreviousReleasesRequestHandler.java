package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addSort;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends ListPageBaseRequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.article, ContentType.bulletin, ContentType.compendium_landing_page};

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return true;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    protected SearchResponseHelper doSearch(HttpServletRequest request, ONSQuery query) throws IOException {
        //default sort is relevance, clear before searching
        query.getSorts().clear();
        addSort(query, SortBy.RELEASE_DATE);
        return super.doSearch(request, query);
    }
}
