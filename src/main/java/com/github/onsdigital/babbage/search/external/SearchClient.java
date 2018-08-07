package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.search.*;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;

public class SearchClient {
    private static HttpClient client = new HttpClient();

    static {
        try {
            client.start();
            Runtime.getRuntime().addShutdownHook(new Shutdown(client));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Request request(String uri) {
        return client.newRequest(uri);
    }

    public static Request get(String uri) {
        return request(uri).method(HttpMethod.GET);
    }

    public static Request post(String uri) {
        return request(uri).method(HttpMethod.POST);
    }

    public static LinkedHashMap<String, SearchResult> search(HttpServletRequest request, String listType) throws Exception {
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();

        final String searchTerm = extractSearchTerm(request);
        final int page = extractPage(request);
        final int pageSize = extractPageSize(request);
        final SortBy sortBy = extractSortBy(request, ContentQuery.DEFAULT_SORT_BY);
        final Set<TypeFilter> typeFilters = extractSelectedFilters(request, ContentQuery.DEFAULT_TYPE_FILTERS, false);

        ListType listTypeEnum = ListType.forString(listType);

        for (SearchType searchType : SearchType.values()) {
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
                    throw new RuntimeException(String.format("Unknown searchType: %s", searchType.getSearchType()));
            }
            SearchResult result = searchQuery.execute();
            results.put(searchType.getResultKey(), result);
        }

        return results;
    }

    public static void stop() throws Exception {
        client.stop();
    }

    static class Shutdown extends Thread {
        /**
         * Class to ensure clean shutdown of HttpClient
         */

        private final HttpClient client;

        public Shutdown(HttpClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                this.client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
