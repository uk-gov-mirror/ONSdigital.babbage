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
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.github.onsdigital.babbage.api.util.SearchUtils.*;

/**
 * Created by bren on 25/11/15.
 */
public class RelatedDataRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private ContentType[] dataFilters = new ContentType[]{ ContentType.dataset_landing_page, ContentType.reference_tables};

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        List<Map> uriList = getRelatedDataUris(uri);
            return isEmpty(uriList) ? buildPageResponse(getRequestType(), null) : listRelatedPage(getRequestType(), uriList, request, dataFilters);
    }


    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, ContentReadException, URISyntaxException {
        List<Map> uriList = getRelatedDataUris(uri);
        return isEmpty(uriList) ? buildDataResponse(getRequestType(), null) : buildDataResponse(getRequestType(),
                SearchUtils.listRelatedPages(uriList, dataFilters));
    }

    private boolean isEmpty(List<Map> relatedDataUris) {
        return relatedDataUris == null || relatedDataUris.isEmpty();
    }

    @Override
    public String getRequestType() {
        return "relateddata";
    }


    private List<Map> getRelatedDataUris(String requestedUri) throws ContentReadException, IOException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri);
        Map<String, Object> objectMap = JsonUtil.toMap(contentResponse.getDataStream());
        if (!isPublication(objectMap.get("type"))) {
            throw new ResourceNotFoundException();
        }
        return (List) objectMap.get("relatedData");
    }

    private boolean isPublication(Object typeName) {
        if (typeName == null) {
            return false;
        }
        ContentType contentType;
        try {
            contentType = ContentType.valueOf(String.valueOf(typeName));
        } catch (IllegalArgumentException e) {
            return false;
        }

        TypeFilter.getPublicationFilters();
        for (TypeFilter typeFilter : TypeFilter.getPublicationFilters()) {
            for (ContentType type : typeFilter.getTypes()) {
                if (type == contentType) {
                    return true;
                }
            }
        }
        return false;
    }

}
