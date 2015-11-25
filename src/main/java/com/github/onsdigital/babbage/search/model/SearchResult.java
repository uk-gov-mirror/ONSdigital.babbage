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
    private List<DocCount> docCounts;

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

    public List<DocCount> getDocCounts() {
        return docCounts;
    }

    public void setDocCounts(List<DocCount> docCounts) {
        this.docCounts = docCounts;
    }

    public void addResult(Map<String, Object> result) {
        results.add(result);
    }

    public void addDocCount(String key, long number) {
        if (docCounts == null) {
            docCounts = new ArrayList<>();
        }
        docCounts.add(new DocCount(key, number));
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public class DocCount {
        private String key;
        private long count;
        DocCount(String key, long count) {
            this.key = key;
            this.count = count;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
