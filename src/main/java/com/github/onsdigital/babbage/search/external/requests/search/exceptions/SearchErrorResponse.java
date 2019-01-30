package com.github.onsdigital.babbage.search.external.requests.search.exceptions;

public class SearchErrorResponse extends Exception {

    public SearchErrorResponse(String response, int code, String context) {
        super(String.format("Received error response from search service: [status=%d, response=%s, context=%s]",
                code, response, context));
    }

}
