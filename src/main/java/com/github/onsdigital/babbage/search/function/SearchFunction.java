package com.github.onsdigital.babbage.search.function;

import com.github.onsdigital.babbage.search.model.SearchResult;

import java.util.Map;

/**
 * Created by bren on 19/01/16.
 */
@FunctionalInterface
public interface SearchFunction {
    Map<String, SearchResult> search();
}
