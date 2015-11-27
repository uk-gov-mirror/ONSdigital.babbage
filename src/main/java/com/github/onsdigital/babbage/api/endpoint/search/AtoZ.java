package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addSort;
import static com.github.onsdigital.babbage.util.RequestUtil.getParam;

/**
 * Created by bren on 19/11/15.
 */
@Api
public class AtoZ extends ListPageBaseRequestHandler  {

    private final static ContentType[] ALLOWED_TYPES = {ContentType.bulletin};


    @GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = URIUtil.cleanUri(request.getRequestURI());
        String requestType = URIUtil.resolveRequestType(uri);
        uri = URIUtil.removeLastSegment(uri);
        if ("data".equals(requestType)) {
            getData(uri, request).apply(response);
        } else {
            get(uri, request).apply(response);
        }
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName().toLowerCase();
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
        if(StringUtils.isEmpty(query.getSearchTerm())) { // sort by title if no search term available
            query.getSorts().clear();
            addSort(query, SortBy.TITLE);
        }
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
