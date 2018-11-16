package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.search.ListType;

public enum SearchEndpoints {

    SEARCH("/search/"),
    SEARCH_ONS("/search/"),
    SPELLING("/spellcheck");

    private String endpoint;

    SearchEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
