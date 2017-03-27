package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.search.model.filter.FirstLetterFilter;
import com.github.onsdigital.babbage.search.model.filter.LatestFilter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.Map;

import static com.github.onsdigital.babbage.search.model.QueryType.COUNTS;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
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
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.first_letter,
                                                                       Lists.newArrayList(SEARCH, COUNTS));
        searchParam.addDocType(ContentType.bulletin)
                   .setAggregationField(Field.title_first_letter.fieldName())
                   .addFilter(new LatestFilter())
                   .addFilter(new FirstLetterFilter(getFirstLetter(request)));

        final Map<String, SearchResult> results = SearchUtils.search(searchParam);


        final SearchResult countSearchResult = results.get(COUNTS.getText());
        Long count = countSearchResult.getDocCounts()
                                      .get(firstLetter);


        if (isNotBlank(firstLetter) && (count == null)) {//no result for selected letter
            //query all not just that letter
            final Map<String, SearchResult> searchOnly = SearchUtils.search(searchParam.setSearchTerm(null));
            results.put(SEARCH.getText(), searchOnly.get(SEARCH.getText()));
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