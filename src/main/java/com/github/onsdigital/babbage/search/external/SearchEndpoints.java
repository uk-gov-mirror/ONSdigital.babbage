package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.search.requests.ListType;

public enum SearchEndpoints {

    SEARCH("/search/"),
    SEARCH_ONS("/search/%s/"),
    SPELLING("/spellcheck");

    private String endpoint;

    SearchEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getEndpointForListType(ListType listType) {
        return String.format(this.endpoint, listType.getEndpoint());
    }
}
