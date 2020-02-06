package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchRendering;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildSearchQuery;
import static com.github.onsdigital.babbage.api.util.SearchUtils.search;
import static com.github.onsdigital.babbage.api.util.SearchUtils.getRedirectWhenTimeSeries;
import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.typeCountsQuery;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchPublication {

    //available allFilters on the page
    private static Set<TypeFilter> publicationFilters = TypeFilter.getPublicationFilters();
    //Counting all types to show counts on tabs
    private static ContentType[] contentTypesToCount = TypeFilter.contentTypes((TypeFilter.getAllFilters()));

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);
        if (searchTerm != null) {
            String timeSeriesRedirect = getRedirectWhenTimeSeries(request, searchTerm);
            if (timeSeriesRedirect != null) {
                new BabbageRedirectResponse(timeSeriesRedirect, appConfig().babbage().getSearchResponseCacheTime())
                        .apply(request, response);
            }
        }
        Map<String, SearchResult> results = search(searchTerm, queries(request, searchTerm), false);
        SearchRendering.buildResponse(request, getClass().getSimpleName(), results).apply(request, response);

    }

    private SearchQueries queries(HttpServletRequest request, String searchTerm) {
        return () -> {
            ONSQuery query = buildSearchQuery(request, searchTerm, publicationFilters);
            return toList(
                    query,
                    typeCountsQuery(query.query()).types(contentTypesToCount)
            );
        };
    }
}
