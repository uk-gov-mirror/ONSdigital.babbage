package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchRendering;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.onsdigital.babbage.api.util.SearchUtils.searchAll;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterDates;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublishedRequestsRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private final static String REQUEST_TYPE = "publishedrequests";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return SearchRendering.buildPageResponse(REQUEST_TYPE, searchAll(queries(request)));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) {
        return SearchRendering.buildDataResponse(REQUEST_TYPE, searchAll(queries(request)));
    }

    private SearchQueries queries(HttpServletRequest request) {
        return () -> toList(
                SearchUtils
                        .buildListQuery(request, filters(request))
                        .types(ContentType.static_foi)
        );
    }

    private SearchFilter filters(HttpServletRequest request) {
        return (listQuery) -> filterDates(request, listQuery);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
