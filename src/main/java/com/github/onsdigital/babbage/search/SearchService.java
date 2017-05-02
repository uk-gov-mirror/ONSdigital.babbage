package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.error.ValidationError;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.github.onsdigital.babbage.search.model.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

    public BabbageResponse listPageWithValidationErrors(String listType, SearchQueries queries,
                                                        List<ValidationError> errors) throws IOException {
        if (errors == null || errors.isEmpty()) {
            return listPage(listType, queries);
        }
        return SearchUtils.listPageWithValidationErrors(listType, queries, errors);
    }

    public BabbageResponse getBabbageResponseListPage(String listType, SearchParam param) throws IOException, URISyntaxException {
        return buildPageResponse(listType, search(param));
    }

    public BabbageResponse listPage(String listType, SearchQueries queries) throws IOException {
        return SearchUtils.listPage(listType, queries);
    }

    public BabbageResponse listJson(String listType, SearchParam param) throws IOException, URISyntaxException {
        return SearchUtils.buildDataResponse(listType, search(param));
    }
}
