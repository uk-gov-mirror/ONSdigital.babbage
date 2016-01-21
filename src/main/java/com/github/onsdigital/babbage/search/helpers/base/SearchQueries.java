package com.github.onsdigital.babbage.search.helpers.base;

import com.github.onsdigital.babbage.search.helpers.ONSQuery;

import java.util.List;

/**
 * Created by bren on 20/01/16.
 */
@FunctionalInterface
public interface SearchQueries {

    List<ONSQuery> buildQueries();
}
