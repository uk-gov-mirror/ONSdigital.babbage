package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.search;

/**
 * Encapsulates some of the static search methods making it easier to test.
 */
public class SearchService {

    private static final SearchService service = new SearchService();

    public static SearchService get() {
        return service;
    }

    public PublishDates extractPublishDates(HttpServletRequest request) throws PublishDatesException {
        return SearchRequestHelper.extractPublishDates(request);
    }

    public BabbageResponse getBabbageResponseListPage(String listType, SearchParam param) throws IOException, URISyntaxException {
        return buildPageResponse(listType, search(param));
    }

    public BabbageResponse listJson(String listType, SearchParam param) throws IOException, URISyntaxException {
        return SearchUtils.buildDataResponse(listType, search(param));
    }
}
