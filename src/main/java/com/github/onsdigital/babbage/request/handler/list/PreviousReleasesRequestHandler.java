package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildDataResponse;
import static com.github.onsdigital.babbage.api.util.SearchUtils.buildPageResponse;
import static com.github.onsdigital.babbage.util.URIUtil.removeLastSegment;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private final static String REQUEST_TYPE = "previousreleases";
    public static final Collection<QueryType> QUERY_TYPES = Lists.newArrayList(QueryType.SEARCH);
    private static Set<TypeFilter> publicationFilters = TypeFilter.getPublicationFilters();

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        assertPageContentType(uri);
        return buildPageResponse(REQUEST_TYPE, queries(request, uri));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, ContentReadException, URISyntaxException {
        assertPageContentType(uri);
        return buildDataResponse(REQUEST_TYPE, queries(request, uri));
    }

    private Map<String, SearchResult> queries(HttpServletRequest request, String uri) throws IOException, URISyntaxException {

        final SearchParam param = SearchParamFactory.getInstance(request, null, QUERY_TYPES)
                                                          .addTypeFilters(publicationFilters)
                                                          .setPrefixURI(uri);
        return SearchUtils.search(param);
    }


    private void assertPageContentType(String uri) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance()
                                                       .getContent(removeLastSegment(removeLastSegment(uri)));
        Map<String, Object> objectMap = JsonUtil.toMap(contentResponse.getDataStream());
        if (!isProductPage(objectMap.get("type"))) {
            throw new ResourceNotFoundException("Requested content's previous releases are not available, uri: " + uri + "");
        }
    }

    private boolean isProductPage(Object type) {
        return ContentType.product_page.name()
                                       .equals(type);
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }


}
