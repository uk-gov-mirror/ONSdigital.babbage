package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.base.AbstractSearchRequest;
import com.github.onsdigital.babbage.search.external.requests.base.SearchClosable;
import com.github.onsdigital.babbage.search.external.requests.base.ShutdownThread;
import com.github.onsdigital.babbage.search.external.requests.search.*;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPage;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSelectedFilters;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSize;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSortBy;

public class SearchClient implements SearchClosable {

    private static SearchClient INSTANCE;

    public static SearchClient getInstance() {
        if (INSTANCE == null) {
            synchronized (SearchClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SearchClient();
                    logEvent().info("initialising external search client");
                    Runtime.getRuntime().addShutdownHook(new ShutdownThread(INSTANCE));
                    logEvent().info("initialisation of external search client completed successfully");
                }
            }
        }
        return INSTANCE;
    }

    private final SearchHttpClient client;

    private SearchClient() {
        this.client = new SearchHttpClient();
    }

    /**
     * Returns a CloseableHttpResponse, WHICH MUST BE CLOSED AFTER USE (e.g try with resources)!!!
     * @param searchRequest
     * @return
     * @throws Exception
     */
    public CloseableHttpResponse execute(AbstractSearchRequest searchRequest) throws Exception {
        HttpRequestBase request = searchRequest.getRequestBase();

        HttpRequestBase requestBase = searchRequest.getRequestBase();
        logEvent()
                .requestID(requestBase)
                .parameter("httpMethod", requestBase.getMethod())
                .uri(requestBase.getURI())
                .info("Executing external search request");

        return this.client.execute(request);
    }

    @Override
    public void close() throws Exception {
        logEvent()
                .info("Closing external search HTTP client");

        this.client.close();
    }

    public LinkedHashMap<String, SearchResult> search(HttpServletRequest request, String listType) throws Exception {
        Map<String, Future<SearchResult>> futures = new HashMap<>();

        final String searchTerm = extractSearchTerm(request);
        final int page = extractPage(request);
        final int pageSize = extractSize(request);
        final SortBy sortBy = extractSortBy(request, ContentQuery.DEFAULT_SORT_BY);

        ListType listTypeEnum = ListType.forString(listType);

        final Set<TypeFilter> typeFilters = extractSelectedFilters(request, listTypeEnum.getTypeFilters(), false);

        SearchType[] searchTypes;
        if (!listTypeEnum.equals(ListType.ONS)) {
            searchTypes = new SearchType[]{SearchType.CONTENT, SearchType.COUNTS};
        } else {
            // Submit content
            searchTypes = SearchType.getBaseSearchTypes();
        }

        for (SearchType searchType : searchTypes) {
            SearchQuery searchQuery;
            switch (searchType) {
                case CONTENT:
                    searchQuery = new ContentQuery(searchTerm, listTypeEnum, page, pageSize, sortBy, typeFilters);
                    break;
                case COUNTS:
                    searchQuery = new TypeCountsQuery(searchTerm, listTypeEnum);
                    break;
                case FEATURED:
                    searchQuery = new FeaturedResultQuery(searchTerm, listTypeEnum);
                    break;
                default:
                    throw new Exception(String.format("Unknown searchType: %s", searchType.getSearchType()));
            }
            // Submit concurrent requests
            Future<SearchResult> future = SearchClientExecutorService.getInstance().submit(searchQuery);
            futures.put(searchType.getResultKey(), future);
        }

        // Wait until complete
        return processFutures(futures);
    }

    private static LinkedHashMap<String, SearchResult> processFutures(Map<String, Future<SearchResult>> futures) throws ExecutionException, InterruptedException {
        // Collect results
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();

        for (String key : futures.keySet()) {
            SearchResult result = futures.get(key).get();
            results.put(key, result);
        }

        return results;
    }
}
