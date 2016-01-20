package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.util.Set;

import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;

/**
 * Created by bren on 21/09/15.
 */
@Api
public class SearchPublication {

    //available allFilters on the page
    private static Set<TypeFilter> publicationFilters = TypeFilter.getPublicationFilters();
    //Counting all types to show counts on tabs
    private static ContentType[] contentTypesToCount = resolveContentTypes(TypeFilter.getAllFilters());

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchTerm = extractSearchTerm(request);
        SearchUtils.search(request, publicationFilters, contentTypesToCount, getClass().getSimpleName(), searchTerm)
                .apply(request, response);

    }
}
