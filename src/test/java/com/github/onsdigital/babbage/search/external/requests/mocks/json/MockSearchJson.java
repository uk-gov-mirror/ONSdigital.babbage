package com.github.onsdigital.babbage.search.external.requests.mocks.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.SearchResult;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MockSearchJson {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Long numberOfResults = 533L;
    private static final int took = 51;

    protected abstract List<Map<String, Object>> getResults() throws IOException;

    protected LinkedHashMap<String, Integer> getDocCounts() {
        return new LinkedHashMap<>();
    }

    private Paginator testPaginator() {
        return new Paginator(54, 10, 1, 10);
    }

    public SearchResult getSearchResult() throws IOException {
        final Map<String, Object> testSearchResponse = new LinkedHashMap<String, Object>() {{
            put("numberOfResults", numberOfResults);
            put("took", took);
            put("results", getResults());
            put("docCounts", getDocCounts());
            put("paginator", testPaginator());
            put("sortBy", SortBy.relevance.name());
        }};

        String json = MAPPER.writeValueAsString(testSearchResponse);
        SearchResult result = MAPPER.readValue(json, SearchResult.class);
        return result;
    }

}
