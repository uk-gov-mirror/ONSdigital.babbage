package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.base.SearchClosable;
import com.github.onsdigital.babbage.search.external.requests.base.ShutdownThread;
import com.github.onsdigital.babbage.search.external.requests.search.requests.ContentQuery;
import com.github.onsdigital.babbage.search.external.requests.search.requests.FeaturedResultQuery;
import com.github.onsdigital.babbage.search.external.requests.search.requests.ListType;
import com.github.onsdigital.babbage.search.external.requests.search.requests.ProxyONSQuery;
import com.github.onsdigital.babbage.search.external.requests.search.requests.SearchQuery;
import com.github.onsdigital.babbage.search.external.requests.search.requests.TypeCountsQuery;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.logging.LogBuilder.logEvent;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPage;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSelectedFilters;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSize;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSortBy;

public class SearchClient implements SearchClosable {

    private static SearchClient INSTANCE;

    public static SearchClient getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (SearchClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SearchClient();
                    logEvent().info("initialising external search client");
                    INSTANCE.start();
                    Runtime.getRuntime().addShutdownHook(new ShutdownThread(INSTANCE));
                    logEvent().info("initialisation of external search client completed successfully");
                }
            }
        }
        return INSTANCE;
    }

    private final HttpClient client;

    public SearchClient() {
        this.client = new HttpClient();
    }

    public void start() throws Exception {
        this.client.start();
    }

    @Override
    public void close() throws Exception {
        this.client.stop();
    }

    public Request request(String uri) {
        return client.newRequest(uri);
    }

    public Request get(String uri) {
        return this.request(uri).method(HttpMethod.GET);
    }

    public Request post(String uri) {
        return this.request(uri).method(HttpMethod.POST);
    }

    public LinkedHashMap<String, SearchResult> proxyQueries(List<ONSQuery> queryList) throws Exception {
        Map<String, Future<SearchResult>> futures = new HashMap<>();

        for (ONSQuery query : queryList) {
            int page, pageSize;

            if (null != query) {
                page = query.page() != null ? query.page() : 1;
                pageSize = query.size();
            } else {
                page = 1;
                pageSize = appConfig().babbage().getResultsPerPage();
            }

            ProxyONSQuery proxyONSQuery = new ProxyONSQuery(query, page, pageSize);
            Future<SearchResult> future = SearchClientExecutorService.getInstance().submit(proxyONSQuery);
            futures.put(query.name(), future);
        }

        return processFutures(futures);
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
