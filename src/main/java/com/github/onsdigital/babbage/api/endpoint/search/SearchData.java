package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static org.apache.commons.lang3.ArrayUtils.addAll;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchData {

    //available dataFilters on the page
    private static Set<TypeFilter> dataFilters = TypeFilter.getDataFilters();
    //Counting all types to show counts on tabs
    private static ContentType[] contentTypesToCount = resolveContentTypes(TypeFilter.getAllFilters());

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);
        SearchUtils.search(request, dataFilters, contentTypesToCount, getClass().getSimpleName(), searchTerm)
                .apply(request, response);
    }
}
