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
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.SearchResults;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.filter.PrefixFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.*;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterUriAndTopics;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.typeCountsQuery;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPublishDates;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.publishedAnyTime;
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
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, BadRequestException, URISyntaxException {
        if (rssService.isRssRequest(request)) {
            return rssService.getDataListFeedResponse(request);
        } else {
                //TODO Count is wrong, no search by dates, filter on doctyoe!
                final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter);
                searchParam
                        .addDocType(ContentType.timeseries) //?? from uri??
                        .addDocType(ContentType.dataset)
                        .addQueryType(QueryType.SEARCH)
                        .addQueryType(QueryType.COUNTS)
                        .addFilter(new PrefixFilter(uri));
                // Filter on dates!!!

                final SearchResults search = SearchUtils.search(searchParam);
                final Map<String, SearchResult> results = new HashMap<>();
                results.put(QueryType.SEARCH.getText(), search.getResults(QueryType.SEARCH));
                results.put(QueryType.COUNTS.getText(), search.getResults(QueryType.COUNTS));
                return SearchUtils.buildPageResponse(REQUEST_TYPE, results);
        }
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, BadRequestException {
        PublishDates publishDates;
        try {
            publishDates = extractPublishDates(request);
        } catch (PublishDatesException ex) {
            publishDates = publishedAnyTime();
        }
        return listJson(REQUEST_TYPE, queries(request, publishDates, uri));
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
