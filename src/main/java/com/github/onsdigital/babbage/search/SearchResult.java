package com.github.onsdigital.babbage.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 07/09/15.
 */
public class SearchResult {

    private long numberOfResults;
    private long took;
    private int currentPage;
    private int numberOfPages;
    private List<Map<String, Object>> results = new ArrayList<>();
    public long getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(long numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public List<Map<String,Object>> getResults() {
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
}
