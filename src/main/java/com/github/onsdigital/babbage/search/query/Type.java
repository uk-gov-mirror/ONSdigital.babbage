package com.github.onsdigital.babbage.search.query;

/**
 * Created by bren on 07/09/15.
 *
 * Represents an elastic search type.
 *
 * Optionally accepts a limit to limit number of results of the type
 *
 */
public class Type {
    private String type;
    private int limit;


    public Type(String type) {
        this.type = type;
    }

    public Type(String type, int limit) {

        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
