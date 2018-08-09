package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchEndpoints;
import com.github.onsdigital.babbage.search.external.SearchType;
import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpScheme;

public abstract class SearchQuery extends AbstractSearchRequest<SearchResult> {

    private final String searchTerm;
    private final ListType listType;
    private final SearchType searchType;

    public SearchQuery(String searchTerm, ListType listType, SearchType searchType) {
        super(SearchResult.class);
        this.searchTerm = searchTerm;
        this.listType = listType;
        this.searchType = searchType;
    }

    protected URIBuilder buildUri() {
        String path = SearchEndpoints.SEARCH_ONS.getEndpointForListType(this.listType) +
                this.searchType.getSearchType();

        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(HttpScheme.HTTP.asString())
                .setHost(HOST)
                .setPath(path)
                .addParameter(SearchParam.QUERY.getParam(), this.searchTerm);

        return uriBuilder;
    }

    @Override
    public String targetUri() {
        return this.buildUri().toString();
    }

    @Override
    protected ContentResponse getContentResponse() throws Exception {
        return super.post().send();
    }

    public enum SearchParam {
        QUERY("q"),
        PAGE("page"),
        SIZE("size"),
        SORT("sort_by"),
        FILTER("filter");

        private String param;

        SearchParam(String param) {
            this.param = param;
        }

        public String getParam() {
            return param;
        }
    }
}
