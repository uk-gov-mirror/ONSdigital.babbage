package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.search.input.SortBy;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractPage;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSearchTerm;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSortBy;
import static com.github.onsdigital.babbage.api.util.SearchUtils.extractSize;

/**
 * Created by guidof on 24/03/17.
 */
public class SearchParamFactory {
    private SearchParamFactory() {
        //FACTORY DO NOT INSTANTIATE
    }

    public static SearchParam getInstance(HttpServletRequest request, SortBy defaultSortBy) {
        return getInstance().setSearchTerm(extractSearchTerm(request))
                     .setSize(extractSize(request))
                     .setPage(extractPage(request))
                     .setSortBy(extractSortBy(request, defaultSortBy));
    }

    public static SearchParam getInstance() {
        return new SearchParam();
    }
}
