package com.github.onsdigital.babbage.search.external.requests.mocks.json;

import com.github.onsdigital.babbage.search.model.ContentType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MockTypeCountsJson extends MockSearchJson {

    @Override
    protected List<Map<String, Object>> getResults() {
        return null;
    }

    @Override
    protected LinkedHashMap<String, Integer> getDocCounts() {
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

}
