package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.HttpRequestUtil;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.List;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.search;
import static com.github.onsdigital.babbage.search.input.TypeFilter.contentTypes;
import static com.github.onsdigital.babbage.search.model.QueryType.COUNTS;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchPublication {

    private static final List<QueryType> BASE_QUERIES = Lists.newArrayList(SEARCH,
                                                                           COUNTS);

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {


        final boolean dataRequest = isDataRequest(request.getRequestURI());


        String[] filters = request.getParameterValues("filter");
        final Set<TypeFilter> typeFilters = HttpRequestUtil.extractFilters(filters,
                                                                           TypeFilter.getPublicationFilters());

        SearchParam param = SearchParamFactory.getInstance(request, SortBy.relevance)
                                              .addQueryTypes(BASE_QUERIES)
                                              .addDocTypes(contentTypes(typeFilters));

        search(dataRequest,
               getClass().getSimpleName(),
               param).apply(request,
                            response);

    }

}
