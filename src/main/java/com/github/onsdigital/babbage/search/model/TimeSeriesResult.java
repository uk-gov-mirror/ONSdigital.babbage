package com.github.onsdigital.babbage.search.model;

/**
 * Created by guidof on 22/03/17.
 */
public class TimeSeriesResult {
    private String uri;
    private String id;
    private String index;
    private String type;

    public String getUri() {
        return uri;
    }

    public TimeSeriesResult setUri(final String uri) {
        this.uri = uri;
        return this;
    }

    public String getId() {
        return id;
    }

    public TimeSeriesResult setId(final String id) {
        this.id = id;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public TimeSeriesResult setIndex(final String index) {
        this.index = index;
        return this;
    }

    public String getType() {
        return type;
    }

    public TimeSeriesResult setType(final String type) {
        this.type = type;
        return this;
    }
}
