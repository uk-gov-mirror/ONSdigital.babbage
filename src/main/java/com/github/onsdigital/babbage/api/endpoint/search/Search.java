package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.api.util.SearchUtils.NamedSearch;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.Set;

import static com.github.onsdigital.babbage.search.helpers.ONSQueryBuilders.contentQuery;
import static com.github.onsdigital.babbage.search.helpers.ONSQueryBuilders.onsQuery;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;

@Api
public class Search {

    //available allFilters on the page
    private static Set<TypeFilter> allFilters = TypeFilter.getAllFilters();
    private static ContentType[] contentTypesToCount = SearchHelper.resolveContentTypes(allFilters);

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);
        ONSQuery topicSearch = onsQuery(contentQuery(searchTerm))
                .types(ContentType.product_page)
                .size(1);
        SearchUtils.search(request, allFilters, contentTypesToCount, getClass().getSimpleName(), searchTerm, new NamedSearch("featuredResult", topicSearch))
                .apply(request, response);
    }


}