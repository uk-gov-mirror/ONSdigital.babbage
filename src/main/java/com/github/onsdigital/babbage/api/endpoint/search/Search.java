package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.search.helpers.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.*;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static com.github.onsdigital.babbage.search.input.TypeFilter.*;
import static java.util.Collections.addAll;
import static org.elasticsearch.index.query.QueryBuilders.functionScoreQuery;

@Api
public class Search {

    //available allFilters on the page
    private static Set<TypeFilter> allFilters = new HashSet<>();
    private static ContentType[] contentTypesToCount = resolveContentTypes(TypeFilter.values());

    static {
        addAll(allFilters,
                BULLETIN,
                ARTICLE,
                COMPENDIA,
                TIME_SERIES,
                DATASETS,
                USER_REQUESTED_DATA,
                METHODOLOGY,
                CORPORATE_INFORMATION
        );
    }

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);

        SearchRequestHelper.get(request, response, getClass().getSimpleName(), searchTerm, () -> {
            Set<TypeFilter> selectedFilters = extractSelectedFilters(request, allFilters);
            ContentType[] selectedContentTypes = resolveContentTypes(selectedFilters);

            DisMaxQueryBuilder baseContentQuery = buildBaseContentQuery(searchTerm);

            FunctionScoreQueryBuilder contentQueryBuilder = boostContentTypes(baseContentQuery);

            ONSQueryBuilder topicSearch = onsQuery(baseContentQuery, ContentType.product_page).size(1);
            ONSQueryBuilder contentSearch = onsQuery(request, contentQueryBuilder, selectedContentTypes);
            ONSQueryBuilder counts = countDocTypes(baseContentQuery, contentTypesToCount);

            if (contentSearch.sortBy() == null) {
                contentSearch.sortBy(SortBy.relevance);
            }

            List<SearchResponseHelper> searchResponseHelpers = SearchHelper.searchMultiple(topicSearch, contentSearch, counts);
            LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
            results.put("featuredResult", searchResponseHelpers.get(0).getResult());
            results.put("result", searchResponseHelpers.get(1).getResult());
            results.put("counts", searchResponseHelpers.get(2).getResult());
            return results;
        });
    }


}