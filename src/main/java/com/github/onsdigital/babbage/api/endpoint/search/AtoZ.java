package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.SearchResults;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.filter.FirstLetterFilter;
import com.github.onsdigital.babbage.search.model.filter.LatestFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by bren on 19/11/15.
 */
@Api
public class AtoZ {

    @GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String firstLetter = getFirstLetter(request);
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter);
        searchParam.addDocType(ContentType.bulletin)
                   .addQueryType(QueryType.SEARCH)
                   .addQueryType(QueryType.COUNTS)
                   .setAggregationField(Field.title_first_letter.fieldName())
                   .addFilter(new LatestFilter())
                   .addFilter(new FirstLetterFilter(getFirstLetter(request)));

        final SearchResults search = SearchUtils.search(searchParam);

        final SearchResult countSearchResult = search.getResults(QueryType.COUNTS);
        Long count = countSearchResult.getDocCounts()
                                      .get(firstLetter);

        final Map<String, SearchResult> results = new HashMap<>();
        results.put(QueryType.COUNTS.getText(), countSearchResult);

        if (isNotBlank(firstLetter) && (count == null)) {//no result for selected letter
            //search all not just that letter
            final SearchResults searchOnly = SearchUtils.search(searchParam.setSearchTerm(null));
            results.put(QueryType.SEARCH.getText(), searchOnly.getResults(QueryType.SEARCH));
        }
        else {
            results.put(QueryType.SEARCH.getText(), search.getResults(QueryType.SEARCH));
        }

        final boolean dataRequest = isDataRequest(request.getRequestURI());
        SearchUtils.buildResponse(dataRequest,
                                  getClass().getSimpleName(),
                                  results)
                   .apply(request, response);

    }


    private String getFirstLetter(HttpServletRequest request) {
        String prefix = StringUtils.trim(getParam(request, "az"));
        if (!StringUtils.isEmpty(prefix)) {
            return prefix.toLowerCase();
        }
        return null;
    }
}