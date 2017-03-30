package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.List;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractPage;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractTypeFilters;
import static com.github.onsdigital.babbage.api.util.SearchUtils.search;
import static com.github.onsdigital.babbage.search.input.TypeFilter.contentTypes;
import static com.github.onsdigital.babbage.search.model.QueryType.COUNTS;
import static com.github.onsdigital.babbage.search.model.QueryType.DEPARTMENTS;
import static com.github.onsdigital.babbage.search.model.QueryType.FEATURED;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;

@Api
public class Search {

    private static final List<QueryType> BASE_QUERIES = Lists.newArrayList(SEARCH, COUNTS);

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final Set<TypeFilter> typeFilters = extractTypeFilters(request, null);
        List<QueryType> queries = Lists.newArrayList(BASE_QUERIES);

        if (extractPage(request) == 1 && CollectionUtils.isEmpty(typeFilters)) {
            queries.add(FEATURED);
            queries.add(DEPARTMENTS);
        }

        final boolean dataRequest = isDataRequest(request.getRequestURI());
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.relevance, queries)
                                                          .addDocTypes(contentTypes(typeFilters));


        search(dataRequest,
               getClass().getSimpleName(),
               searchParam).apply(request, response);
    }

}