package com.github.onsdigital.babbage.search.model.filter;

/**
 * Created by guidof on 23/03/17.
 */
public class LatestFilter implements Filter {
    private final String key = "latest";
    private final String value = "true";

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
