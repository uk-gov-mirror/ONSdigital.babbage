package com.github.onsdigital.babbage.search.external.requests.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.onsdigital.babbage.search.external.SearchType;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Replaces the internal content query by executing a HTTP request against the dp-conceptual-search content API
 */
public class ContentQuery extends SearchQuery {

    public static final SortBy DEFAULT_SORT_BY = SortBy.relevance;
    public static final Set<TypeFilter> DEFAULT_TYPE_FILTERS = TypeFilter.getAllFilters();

    private final int page;
    private final int pageSize;
    private final SortBy sortBy;
    private final Set<TypeFilter> typeFilters;

    public ContentQuery(String searchTerm, ListType listType, int page, int pageSize) {
        this(searchTerm, listType, page, pageSize, DEFAULT_SORT_BY, DEFAULT_TYPE_FILTERS);
    }

    public ContentQuery(String searchTerm, ListType listType, int page, int pageSize, SortBy sortBy) {
        this(searchTerm, listType, page, pageSize, sortBy, DEFAULT_TYPE_FILTERS);
    }

    public ContentQuery(String searchTerm, ListType listType, int page, int pageSize, Set<TypeFilter> typeFilters) {
        this(searchTerm, listType, page, pageSize, DEFAULT_SORT_BY, typeFilters);
    }

    public ContentQuery(String searchTerm, ListType listType, int page, int pageSize, SortBy sortBy, Set<TypeFilter> typeFilters) {
        super(searchTerm, listType, SearchType.CONTENT);
        this.page = page;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.typeFilters = typeFilters;
    }

    private Set<String> contentTypeFilters() {
        Set<String> filters = new HashSet<>();
        for (TypeFilter typeFilter : this.typeFilters) {
            for (ContentType contentType : typeFilter.getTypes()) {
                filters.add(contentType.name());
            }
        }

        return filters;
    }

    private String contentTypeFiltersAsString() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this.contentTypeFilters());
    }

    @Override
    protected URIBuilder buildUri() {
        return super.buildUri()
                .addParameter(SearchParam.PAGE.getParam(), String.valueOf(this.page))
                .addParameter(SearchParam.SIZE.getParam(), String.valueOf(this.pageSize));
    }

    @Override
    protected ContentResponse getContentResponse() throws Exception {
        final String filterString = this.contentTypeFiltersAsString();
        final Map<String, String> content = new HashMap<String, String>() {{
            put(SearchParam.FILTER.getParam(), filterString);
            put(SearchParam.SORT.getParam(), sortBy.name());
        }};

        Request request = super.post()
                .content(new StringContentProvider(MAPPER.writeValueAsString(content)));
        return request.send();
    }
}
