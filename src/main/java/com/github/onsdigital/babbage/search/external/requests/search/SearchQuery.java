package com.github.onsdigital.babbage.search.external.requests.search;

import com.github.onsdigital.babbage.search.external.SearchEndpoints;
import com.github.onsdigital.babbage.search.external.SearchType;
import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.http.HttpScheme;

/**
 * Class for querying the dp-conceptual-search APIs
 */
public abstract class SearchQuery extends AbstractSearchRequest<SearchResult> {

    protected final String searchTerm;
    private final ListType listType;
    private final SearchType searchType;
    private URIBuilder uriBuilder;

    public SearchQuery(String searchTerm, ListType listType, SearchType searchType) {
        super(SearchResult.class);
        this.searchTerm = searchTerm;
        this.listType = listType;
        this.searchType = searchType;
    }

    /**
     * Method to build the target URI with desired URL parameters
     * @return
     */
    @Override
    public URIBuilder targetUri() {
        if (null == this.uriBuilder) {
            String path = SearchEndpoints.SEARCH_ONS.getEndpointForListType(this.listType) +
                    this.searchType.getSearchType();

            uriBuilder = new URIBuilder()
                    .setScheme(HttpScheme.HTTP.asString())
                    .setHost(HOST)
                    .setPath(path)
                    .addParameter(SearchParam.QUERY.getParam(), this.searchTerm);
        }

        return uriBuilder;
    }

    /**
     * Executes a HTTP GET request with type filters and sort options specified as a JSON payload
     * @return
     * @throws Exception
     */
    @Override
    public HttpRequestBase getRequestBase() throws Exception {
        return this.get();
    }

    /**
     * Enum of available URL parameters
     */
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
