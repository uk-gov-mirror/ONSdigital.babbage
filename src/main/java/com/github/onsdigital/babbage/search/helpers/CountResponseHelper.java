package com.github.onsdigital.babbage.search.helpers;

import org.elasticsearch.action.search.SearchResponse;

/**
 * Created by bren on 07/09/15.
 */
public class CountResponseHelper {

    private SearchResponse response;
    public CountResponseHelper(SearchResponse response) {
        this.response = response;
    }

    public long getCount() {
        return response.getHits().getTotalHits();
    }

}
