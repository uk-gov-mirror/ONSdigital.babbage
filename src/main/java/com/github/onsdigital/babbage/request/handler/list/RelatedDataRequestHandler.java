package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.search.model.field.FilterableField.uri;

/**
 * Created by bren on 25/11/15.
 */
public class RelatedDataRequestHandler extends ListPageBaseRequestHandler implements RequestHandler{

    private final static ContentType[] ALLOWED_TYPES = {
            ContentType.dataset_landing_page,
            ContentType.reference_tables};


    @Override
    protected ONSQuery createQuery(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        try (ContentStream stream = ContentClient.getInstance().getContentStream(requestedUri)) {
            Map<String, Object> objectMap = JsonUtil.toMap(stream.getDataStream());
            if (!isPublication(objectMap.get(SearchableField.type.name()))) {
                throw new ResourceNotFoundException("Requested content's previous releases are not available, uri: " + requestedUri + "");
            }
            ONSQuery query = super.createQuery(requestedUri, request);

            List<Map> list = (List) objectMap.get("relatedData");
            if (list == null || list.isEmpty()) {
                throw new ResourceNotFoundException("Content does not have any related data");
            }
            String[] uriArray = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                uriArray[i] = (String) list.get(i).get(uri.name());

            }
            SearchRequestHelper.addTermsFilter(query, uri, uriArray);
            return query;
        }
    }

    @Override
    protected SearchResponseHelper doSearch(HttpServletRequest request, ONSQuery query) throws IOException {
        return super.doSearch(request, query);
    }

    private boolean isPublication(Object type) {
        if (type == null) {
            return false;
        }
        return ContentType.isTypeIn(String.valueOf(type), ContentType.article, ContentType.bulletin);
    }

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public String getRequestType() {
        return "relateddata";
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }

    @Override
    protected boolean isListTopics() {
        return false;
    }
}
