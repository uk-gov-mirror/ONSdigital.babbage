package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.util.RequestUtil.getParam;

/**
 * Created by bren on 19/11/15.
 */
public class AtoZRequestHandler extends ListPageBaseRequestHandler implements RequestHandler {

    private final static String REQUEST_TYPE = "atoz";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.bulletin};

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }

    @Override
    protected ONSQuery createQuery(String requestedUri, HttpServletRequest request) {
        ONSQuery query = super.createQuery(requestedUri, request);
        SearchRequestHelper.addPrefixFilter(query, FilterableField.title_raw, getTitlePrefix(request));
        return query;
    }

    private String getTitlePrefix(HttpServletRequest request) {
        String prefix = StringUtils.trim(getParam(request, "az"));
        if (StringUtils.isEmpty(prefix)) {
            throw new ResourceNotFoundException("Title prefix is not given");
        }
        return prefix;
    }

    @Override
    protected boolean isFilterLatest(HttpServletRequest request) {
        return true;
    }
}
