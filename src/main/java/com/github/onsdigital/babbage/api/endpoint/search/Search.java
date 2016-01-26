package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.List;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildSearchQuery;
import static com.github.onsdigital.babbage.api.util.SearchUtils.search;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.*;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPage;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static org.apache.commons.lang.ArrayUtils.isEmpty;

@Api
public class Search {

    //available allFilters on the page
    private static Set<TypeFilter> allFilters = TypeFilter.getAllFilters();
    private static ContentType[] contentTypesToCount = TypeFilter.contentTypes(allFilters);


    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);
        search(request, getClass().getSimpleName(), searchTerm, queries(request, searchTerm))
                .apply(request, response);
    }

    private SearchQueries queries(HttpServletRequest request, String searchTerm) {
        List<ONSQuery> queries = toList(
                buildSearchQuery(request, searchTerm, allFilters),
                typeCountsQuery(contentQuery(searchTerm)).types(contentTypesToCount)
        );
        String[] filter = request.getParameterValues("filter");
        if (extractPage(request) == 1 && isEmpty(filter)) {
            queries.add(bestTopicMatchQuery(searchTerm).name("featuredResult"));
        }
        return () -> queries;
    }
}