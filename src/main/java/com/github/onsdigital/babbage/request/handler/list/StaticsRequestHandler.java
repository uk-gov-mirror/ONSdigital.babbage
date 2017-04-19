package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.builders.ONSFilterBuilders;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildDataResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;

public class StaticsRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private final static String REQUEST_TYPE = "staticlist";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return buildPageResponse(REQUEST_TYPE, queries(request));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, URISyntaxException {
        return buildDataResponse(REQUEST_TYPE, queries(request));
    }

    private Map<String, SearchResult> queries(HttpServletRequest request) throws IOException, URISyntaxException {
        final SearchParam params = SearchParamFactory.getInstance(request, SortBy.release_date, Collections.singleton(QueryType.SEARCH));
        params.addDocTypes(ContentType.static_page);
        return SearchUtils.search(params);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
