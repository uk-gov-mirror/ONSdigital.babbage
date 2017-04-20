package com.github.onsdigital.babbage.search.model.filter;

/**
 * Created by edwarg1 on 20/04/2017.
 */
public class PublishedFilter implements Filter {

    @Override
    public String getKey() {
        return "published";

    }

    @Override
    public String getValue() {
        return "true";
    }
}
