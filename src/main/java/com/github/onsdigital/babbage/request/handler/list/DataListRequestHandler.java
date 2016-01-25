package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.builders.ONSFilterBuilders;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.listJson;
import static com.github.onsdigital.babbage.api.util.SearchUtils.listPage;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterDates;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterUriAndTopics;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.typeCountsQuery;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DataListRequestHandler implements ListRequestHandler {

    private final static String REQUEST_TYPE = "datalist";
    private static Set<TypeFilter> dataFilters = TypeFilter.getDataFilters();
    //    private static ContentType[] contentTypesToCount = addAll(resolveContentTypes(dataFilters), resolveContentTypes(TypeFilter.getPublicationFilters()));
    private static ContentType[] contentTypesToCount = TypeFilter.contentTypes(dataFilters);

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return listPage(REQUEST_TYPE, queries(request, uri));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException {
        return listJson(REQUEST_TYPE, queries(request, uri));
    }

    private SearchQueries queries(HttpServletRequest request, String uri) {
        ONSQuery listQuery = SearchUtils.buildListQuery(request, dataFilters, filters(request, uri));
        return () -> toList(
                listQuery,
                typeCountsQuery(listQuery.query()).types(contentTypesToCount)
        );
    }

    private SearchFilter filters(HttpServletRequest request, String uri) {
        return (listQuery) -> {
            filterUriAndTopics(uri, listQuery);
            filterDates(request, listQuery);
        };
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
