package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MockedTypeCountsResponse extends MockedSearchResponse {

    @Override
    public LinkedHashMap<String, Integer> getDocCounts() {
        final LinkedHashMap<String, Integer> docCounts = new LinkedHashMap<String, Integer>() {{
            put(ContentType.timeseries.name(), 360);
            put(ContentType.bulletin.name(), 62);
            put(ContentType.article.name(), 43);
            put(ContentType.article_download.name(), 21);
            put(ContentType.dataset_landing_page.name(), 20);
            put(ContentType.static_foi.name(), 12);
            put(ContentType.static_methodology_download.name(), 5);
            put(ContentType.static_methodology.name(), 4);
            put(ContentType.static_adhoc.name(), 3);
            put(ContentType.static_page.name(), 2);
        }};

        return docCounts;
    }

    @Override
    public SearchResult getSearchResult() throws IOException {
        final Map<String, Object> testSearchResponse = new LinkedHashMap<String, Object>() {{
            put("numberOfResults", numberOfResults);
            put("docCounts", getDocCounts());
        }};

        String testJson = MAPPER.writeValueAsString(testSearchResponse);

        SearchResult result = MAPPER.readValue(testJson, SearchResult.class);
        return result;
    }
}
