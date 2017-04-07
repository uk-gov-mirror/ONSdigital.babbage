package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.builders.ONSQueryBuilders;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.SearchResults;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.filter.FirstLetterFilter;
import com.github.onsdigital.babbage.search.model.filter.LatestFilter;
import com.github.onsdigital.babbage.search.model.filter.PrefixFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildListQuery;
import static com.github.onsdigital.babbage.api.util.SearchUtils.list;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterDates;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterTopic;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by bren on 25/11/15.
 */
@Api
public class TimeSeriesTool {

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final boolean dataRequest = isDataRequest(request.getRequestURI());
        final String topic = request.getParameter("topic");
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter, Collections.singleton(QueryType.SEARCH));
        searchParam.addDocType(ContentType.timeseries)
                .addFilter(new PrefixFilter(topic));
        // Filter on dates!!!

        final Map<String, SearchResult> search = SearchUtils.search(searchParam);
        final Map<String, SearchResult> results = new HashMap<>();


        results.put(QueryType.SEARCH.getText(), search.get(QueryType.SEARCH));


        SearchUtils.buildResponse(dataRequest,
                getClass().getSimpleName(),
                results)
                .apply(request, response);
    }

    private SearchQueries queries(HttpServletRequest request) {
        return () -> ONSQueryBuilders.toList(
                buildListQuery(request, filters(request))
                        .types(ContentType.timeseries)
        );
    }

    private SearchFilter filters(HttpServletRequest request) {
        return (listQuery) -> {
            filterTopic(request, listQuery);
            filterDates(request, listQuery);
        };
    }

}
