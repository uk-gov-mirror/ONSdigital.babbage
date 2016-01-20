package com.github.onsdigital.babbage.search.helpers;

import org.elasticsearch.index.query.BoolQueryBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 20/01/16.
 */
public interface ListFilter {
    /**
     * Applies filter to given boolean query based requested filters.
     *
     * @param boolQueryBuilder
     * @return
     */
    void filter(HttpServletRequest request, String uri, BoolQueryBuilder boolQueryBuilder);
}
