package com.github.onsdigital.babbage.search.helpers.base;

import org.elasticsearch.index.query.BoolQueryBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bren on 20/01/16.
 */
@FunctionalInterface
public interface SearchFilter {
    /**
     * Applies filters to requested query
     *
     * @param boolQueryBuilder
     * @return
     */
    void filter(BoolQueryBuilder boolQueryBuilder);
}
