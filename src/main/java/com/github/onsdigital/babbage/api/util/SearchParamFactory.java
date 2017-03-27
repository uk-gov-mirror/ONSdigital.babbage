package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractPage;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSearchTerm;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSortBy;
import static com.github.onsdigital.babbage.api.util.SearchUtils.extractSize;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPublishDates;

/**
 * Created by guidof on 24/03/17.
 */
public class SearchParamFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchParamFactory.class);

    private SearchParamFactory() {
        //FACTORY DO NOT INSTANTIATE
    }

    public static SearchParam getInstance(HttpServletRequest request, SortBy defaultSortBy, Collection<QueryType> queryTypes) {

        PublishDates publishDates = null;
        try {
            publishDates = extractPublishDates(request);
        }
        catch (PublishDatesException e) {
            LOGGER.info("getData([uri, request]) : could not extract PublishDates", e);
        }

        return getInstance().setSearchTerm(extractSearchTerm(request))
                            .setSize(extractSize(request))
                            .setPage(extractPage(request))
                            .setSortBy(extractSortBy(request, defaultSortBy))
                            .setPublishDates(publishDates)
                            .addQueryTypes(queryTypes);
    }

    public static SearchParam getInstance() {
        return new SearchParam();
    }
}
