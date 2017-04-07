package com.github.onsdigital.babbage.search.model.filter;

/**
 * Filter on the uri prefix
 */
public class PrefixFilter implements Filter {

    private final String key = "uriPrefix";

    private final String value;

    public PrefixFilter(final String prefix) {
        this.value = prefix;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
