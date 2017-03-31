package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildDataResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublishedRequestsRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishedRequestsRequestHandler.class);

    private final static String REQUEST_TYPE = "publishedrequests";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return buildPageResponse(REQUEST_TYPE, search(request));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, URISyntaxException {
        return buildDataResponse(REQUEST_TYPE, search(request));
    }

    private Map<String, SearchResult> search(final HttpServletRequest request) throws IOException, URISyntaxException {
        final SearchParam param = SearchParamFactory.getInstance(request, null, Lists.newArrayList(QueryType.SEARCH))
                                                    .addDocType(ContentType.static_foi);
        return SearchUtils.search(param);
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
