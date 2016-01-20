package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.paginator.Paginator;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.search.model.field.FilterableField.uri;

/**
 * Created by bren on 25/11/15.
 */
public class RelatedDataRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {
            ContentType.dataset_landing_page,
            ContentType.reference_tables};


    @Override
    protected LinkedHashMap<String, Object> prepareData(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(requestedUri);
        Map<String, Object> objectMap = JsonUtil.toMap(contentResponse.getDataStream());
        if (!isPublication(objectMap.get(SearchableField.type.name()))) {
            throw new ResourceNotFoundException("Requested content's previous releases are not available, uri: " + requestedUri + "");
        }
        List<Map> list = (List) objectMap.get("relatedData");
        if (list == null || list.isEmpty()) {
            return getBaseData(request);//render empty page without search
        }
        String[] uriArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            uriArray[i] = (String) list.get(i).get(uri.name());

        }

        ONSQuery query = createQuery(requestedUri, request);
        SearchRequestHelper.addTermsFilter(query, uri, uriArray);
        List<SearchResponseHelper> responseHelpers = doSearch(request, query);
        SearchResponseHelper responseHelper = responseHelpers.iterator().next();
        Paginator.assertPage(query.getPage(), responseHelper);
        return resolveListData(request, query, responseHelper, null);
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
