package com.github.onsdigital.babbage.search.external;

public enum SearchEndpoints {

    SEARCH("/search/%s/");

    private String endpoint;

    SearchEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint(String listType) {
        return String.format(this.endpoint, listType);
    }
}
