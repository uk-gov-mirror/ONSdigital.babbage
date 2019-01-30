package com.github.onsdigital.babbage.search.external;

public enum SearchEndpoints {

    SEARCH("/search/"),
    SPELLING("/spellcheck");

    private String endpoint;

    SearchEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
