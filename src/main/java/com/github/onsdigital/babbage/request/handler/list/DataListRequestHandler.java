package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;

import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;

import com.github.onsdigital.babbage.search.model.SearchResult;

import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildDataResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;
import static com.github.onsdigital.babbage.search.model.QueryType.COUNTS;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;

/**
 * Render a list page for bulletins under the given URI.
 */
public class DataListRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private final static String REQUEST_TYPE = "datalist";
    private static Set<TypeFilter> dataFilters = TypeFilter.getDataFilters();
    private static ContentType[] contentTypesToCount = TypeFilter.contentTypes(dataFilters);

    private RssService rssService = RssService.getInstance();

    @Override
    public BabbageResponse get(String uri,
                               HttpServletRequest request) throws IOException, BadRequestException, URISyntaxException {
        final SearchParam params = SearchParamFactory.getInstance(request, null, Lists.newArrayList(SEARCH, COUNTS))
                                                     .addTopic(uri)
                                                     .setPrefixURI(uri);

        if (params.getDocTypes().isEmpty()) {
            params.addTypeFilters(TypeFilter.getDataFilters());
        }

        if (params.isRssFeed()) {
            params.setRequestType(REQUEST_TYPE);
            return rssService.getDataListFeedResponse(params, uri);
        }
        else {
            params.addTopic(uri)
                  .setPrefixURI(uri);
            final Map<String, SearchResult> search = SearchUtils.search(params);
            return buildPageResponse(REQUEST_TYPE, search);
        }
    }

    @Override
    public BabbageResponse getData(String uri,
                                   HttpServletRequest request) throws IOException, BadRequestException, URISyntaxException {
        final SearchParam params = SearchParamFactory.getInstance(request, null, Lists.newArrayList(SEARCH))
                                                     .addTopic(uri)
                                                     .setPrefixURI(uri)
                                                     .addTypeFilters(TypeFilter.getDataFilters());
        final Map<String, SearchResult> search = SearchUtils.search(params);
        return buildDataResponse(REQUEST_TYPE, search);

    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
