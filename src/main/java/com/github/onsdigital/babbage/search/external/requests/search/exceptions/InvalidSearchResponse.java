package com.github.onsdigital.babbage.search.external.requests.search.exceptions;

public class InvalidSearchResponse extends Exception {

    public InvalidSearchResponse(String response, int code) {
        super(String.format("Received invalid response JSON from search service: [status=%d, response=%s]",
                code, response));
    }

}
