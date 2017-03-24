package com.github.onsdigital.babbage.search.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by guidof on 20/03/17.
 */
public class SearchResults {


    private final Map<QueryType, SearchResult> results = new EnumMap<>(QueryType.class);

    SearchResults() {
        //ONLY CONSTRUCTED BY THE FACTORY
    }

    public SearchResults addResult(QueryType type, SearchResult result) {
        results.put(type,
                    result);
        return this;
    }

    public SearchResult getResults(QueryType type) {
        return results.get(type);
    }

    public Collection<SearchResult> getResults() {
        return results.values();
    }
}
