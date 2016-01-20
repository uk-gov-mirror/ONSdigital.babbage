package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addSort;
import static com.github.onsdigital.babbage.util.URIUtil.removeLastSegment;

/**
 * Render a list page for the given URI.
 */
public class PreviousReleasesRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "previousreleases";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.article, ContentType.article_download, ContentType.bulletin, ContentType.compendium_landing_page};

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return true;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    protected LinkedHashMap<String, Object> prepareData(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(removeLastSegment(removeLastSegment(requestedUri)));
        Map<String, Object> objectMap = JsonUtil.toMap(contentResponse.getDataStream());
        if (!isProductPage(objectMap.get("type"))) {
            throw new ResourceNotFoundException("Requested content's previous releases are not available, uri: " + requestedUri + "");
        }
        return super.prepareData(requestedUri, request);
    }


    private boolean isProductPage(Object type) {
        return ContentType.product_page.name().equals(type);
    }

    @Override
    protected List<SearchResponseHelper> doSearch(HttpServletRequest request, ONSQuery... queries) throws IOException, ContentReadException {
        ONSQuery query = queries[0];
        //default sort is relevance, clear before searching
        query.getSorts().clear();
        addSort(query, SortBy.RELEASE_DATE);
        return super.doSearch(request, query);
    }

}
