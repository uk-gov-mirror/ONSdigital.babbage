package com.github.onsdigital.babbage.search.external.requests.search.exceptions;

public class UnknownListTypeException extends Exception {

    public UnknownListTypeException(String listType) {
        super(String.format("Unknown listType: %s", listType));
    }

}
