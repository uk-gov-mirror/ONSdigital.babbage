package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.query.SortOrder;
import com.github.onsdigital.content.page.base.PageType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends ListPageBaseRequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";

    @Override
    public String[] getAllowedTypes() {
        return new String[]{PageType.article.toString(), PageType.bulletin.toString(), PageType.compendium_landing_page.toString()};
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }


    @Override
    protected SearchResponseHelper list(String uri, int page, HttpServletRequest request) throws IOException {
        ONSQueryBuilder queryBuilder = new ONSQueryBuilder()
                .setUriPrefix(uri)
                .addSort("releaseDate", SortOrder.DESC);
        SearchResponseHelper searchResponseHelper = SearchService.getInstance().search(queryBuilder);
        return searchResponseHelper;
    }

    @Override
    protected boolean isPaginated() {
        return false;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
