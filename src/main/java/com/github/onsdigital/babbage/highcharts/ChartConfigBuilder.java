package com.github.onsdigital.babbage.highcharts;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides convenient fluent interface for creating chart config data.
 */
public class ChartConfigBuilder {

    static final String URI_PARAM = "uri";
    static final String WIDTH_PARAM = "width";

    static final String TITLE_PARAM = "title";
    static final String SHOW_TITLE_PARAM = "showTitle";

    static final String SUBTITLE_PARAM = "subtitle";
    static final String SHOW_SUBTITLE_PARAM = "showSubTitle";

    static final String SOURCE_PARAM = "source";
    static final String SHOW_SOURCE_PARAM = "showSource";

    static final String NOTES_PARAM = "notes";
    static final String SHOW_NOTES_PARAM = "showNotes";

    private HashMap<String, Object> additionalData;

    public ChartConfigBuilder() {
        this.additionalData = new HashMap<>();
    }

    public Map<String, Object> getMap() {
        return this.additionalData;
    }

    public ChartConfigBuilder width(Integer width) {
        this.additionalData.put(WIDTH_PARAM, width);
        return this;
    }

    public ChartConfigBuilder showTitle(HttpServletRequest r) {
        this.additionalData.put(SHOW_TITLE_PARAM, getBooleanParam(r, TITLE_PARAM));
        return this;
    }

    public ChartConfigBuilder showSubTitle(HttpServletRequest r) {
        this.additionalData.put(SHOW_SUBTITLE_PARAM, getBooleanParam(r, SUBTITLE_PARAM));
        return this;
    }

    public ChartConfigBuilder showSource(HttpServletRequest r) {
        this.additionalData.put(SHOW_SOURCE_PARAM, getBooleanParam(r, SOURCE_PARAM));
        return this;
    }

    public ChartConfigBuilder showNotes(HttpServletRequest r) {
        this.additionalData.put(SHOW_NOTES_PARAM, getBooleanParam(r, NOTES_PARAM));
        return this;
    }

    private boolean getBooleanParam(HttpServletRequest request, String parameter) {
        return Boolean.valueOf(request.getParameter(parameter));
    }

    @Override
    public boolean equals(Object o) {
        return this.additionalData.equals(o);
    }

    @Override
    public int hashCode() {
        return this.additionalData.hashCode();
    }
}
