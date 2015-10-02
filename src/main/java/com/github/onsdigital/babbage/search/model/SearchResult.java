package com.github.onsdigital.babbage.search.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 07/09/15.
 */
public class SearchResult {

    private Long numberOfResults;
    private long took;
    private List<Map<String, Object>> results = new ArrayList<>();

    public Long getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(long numberOfResults) {
        //do not set number of results if zero,so it does not appear in json
        if (numberOfResults > 0) {
            this.numberOfResults = numberOfResults;
        }
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }

    public void addResult(Map<String, Object> result) {
        results.add(result);
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }
}
