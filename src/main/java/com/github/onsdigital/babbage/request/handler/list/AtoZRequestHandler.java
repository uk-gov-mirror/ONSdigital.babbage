package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

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
    protected ONSQuery createQuery(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        ONSQuery query = super.createQuery(requestedUri, request);
        String titlePrefix = getTitlePrefix(request);
        if(titlePrefix != null) {
            SearchRequestHelper.addTermFilter(query, FilterableField.title_first_letter, titlePrefix);
        }
        query.addAggregation(buildStartsWithAggregation());
        return query;
    }

    private AggregationBuilder buildStartsWithAggregation() {
        return AggregationBuilders.global("count_by_starts_with")
                .subAggregation(new TermsBuilder("starts_with").field(FilterableField.title_first_letter.name()).size(0));
    }

    private String getTitlePrefix(HttpServletRequest request) {
        String prefix = StringUtils.trim(getParam(request, "az"));
        if (!StringUtils.isEmpty(prefix)) {
            return prefix.toLowerCase();
        }
        return null;

    }

    @Override
    protected boolean isFilterLatest(HttpServletRequest request) {
        return true;
    }

}
