package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildDataResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterUriAndTopics;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.typeCountsQuery;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

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
        final SearchParam params = SearchParamFactory.getInstance(request, null, Lists.newArrayList(SEARCH));
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
                                                     .setPrefixURI(uri);
        final Map<String, SearchResult> search = SearchUtils.search(params);
        return buildDataResponse(REQUEST_TYPE, search);

    }

    private SearchQueries queries(HttpServletRequest request, PublishDates publishDates, String uri) {
        ONSQuery listQuery = SearchUtils.buildListQuery(request, dataFilters, filters(publishDates, uri));
        return () -> toList(
                listQuery,
                typeCountsQuery(listQuery.query()).types(contentTypesToCount)
                           );
    }

    private SearchFilter filters(PublishDates publishDates, String uri) {
        return (listQuery) -> {
            filterUriAndTopics(uri, listQuery);
            listQuery.filter(rangeQuery(Field.releaseDate.fieldName())
                                     .from(publishDates.publishedFrom())
                                     .to(publishDates.publishedTo()));
        };
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
