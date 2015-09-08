package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.SearchResult;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * Created by bren on 07/09/15.
 */
public class SearchResponseHelper {

    SearchResponse response;
    SearchResult result;

    public SearchResponseHelper(SearchResponse response) {
        this.response = response;
        this.result = buildResult();
    }

    public long getNumberOfResults() {
        return response.getHits().getTotalHits();
    }

    public SearchResult getResult() {
        return this.result;
    }

    private SearchResult buildResult() {
        SearchResult searchResult = new SearchResult();
        searchResult.setNumberOfResults(getNumberOfResults());
        searchResult.setTook(response.getTookInMillis());

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            searchResult.addResult(hit.getSource());
        }

        return searchResult;
    }

    public String toJson() {
        return JsonUtil.toJson(getResult());
    }


}
