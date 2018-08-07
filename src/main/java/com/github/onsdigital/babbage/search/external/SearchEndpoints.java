package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.search.ListType;

public enum SearchEndpoints {

    SEARCH("/search/%s/");

    private String endpoint;

    SearchEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint(ListType listType) {
        return String.format(this.endpoint, listType.getEndpoint());
    }
}
