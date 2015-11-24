package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Api
public class Search extends ListPageBaseRequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {
            ContentType.bulletin,
            ContentType.article,
            ContentType.compendium_landing_page,
            ContentType.timeseries,
            ContentType.dataset_landing_page,
            ContentType.reference_tables,
            ContentType.static_adhoc,
            ContentType.static_methodology,
            ContentType.static_qmi,
            ContentType.static_article,
            ContentType.static_foi,
            ContentType.static_page,
            ContentType.static_landing_page//methodology
    };


    @Override
    public BabbageResponse getData(String requestedUri, HttpServletRequest request) throws Exception {
        String query = extractSearchTerm(request);
        if (isEmpty(query)) {
            return new BabbageStringResponse("Please specify uri", MediaType.TEXT_PLAIN);
        } else {
            BabbageResponse timeSeriesResponse = getTimeSeriesResponse(query);
            if (timeSeriesResponse != null) {
                return timeSeriesResponse;
            }
            return super.getData(requestedUri, request);
        }
    }

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        String query = extractSearchTerm(request);
        if (isEmpty(query)) {
            renderEmptyPage(request, response);
        } else {
            BabbageResponse result = getTimeSeriesResponse(query);
            if (result != null) {
                result.apply(response);
            } else {
                result =  super.get(cleanUri(request.getRequestURI()), request);
                result.apply(response);
            }
        }
        return null;
    }

    /**
     * Searches agains time series cdid for an exact match , if found creates a redirect response for the client
     *
     * @param query
     * @return
     * @throws IOException
     */
    private BabbageResponse getTimeSeriesResponse(String query) throws IOException {
        String uri = searchTimeSeriesUri(query);
        if (uri != null) {
            return new BabbageRedirectResponse(uri);
        }
        return null;
    }

    private void renderEmptyPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String html = TemplateService.getInstance().renderContent(getBaseData(request));
        BabbageResponse babbageResponse = new BabbageStringResponse(html, TEXT_HTML);
        babbageResponse.apply(response);
    }


    //Counts time series for featured result
    private long countTimeSeries(String uri) {
        System.out.println("Counting time series starting with uri " + uri);
        ONSQuery query = new ONSQuery(ContentType.timeseries.name());
        addPrefixFilter(query, FilterableField.uri, uri);
        CountResponseHelper countResponse = SearchService.getInstance().count(query);
        return countResponse.getCount();
    }

    //returns uri for time series if there is a cdid match
    private String searchTimeSeriesUri(String query) throws IOException {
        ONSQuery onsQuery = new ONSQuery(ContentType.timeseries.name()).setSize(1);
        addTermFilter(onsQuery, FilterableField.cdid, query.toLowerCase());

        SearchResponseHelper searchResponseHelper = SearchService.getInstance().search(onsQuery);
        if (searchResponseHelper.getNumberOfResults() > 0) {
            Map<String, Object> timeSeries = searchResponseHelper.getResult().getResults().iterator().next();
            return (String) timeSeries.get(FilterableField.uri.name());
        }
        return null;
    }

    private ONSQuery buildFeaturedResultQuery(String query) throws IOException {
        ONSQuery onsquery = new ONSQuery(ContentType.product_page.name())
                .setSize(1)
                .setSearchTerm(query)
                .setHighLightFields(true);
        SearchRequestHelper.addFields(onsquery, SearchableField.values());
        return onsquery;
    }

    private boolean isFiltered(HttpServletRequest request) {
        return request.getParameterValues("filter") != null;
    }

    private boolean isTimeSeriesRequested(HttpServletRequest request) {
        return request.getParameter("time_series") != null;
    }

    private boolean isStaticsRequested(HttpServletRequest request) {
        return request.getParameter("includeStatics") != null;
    }

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    protected boolean isListTopics() {
        return false;
    }

    @Override
    public boolean isLocalisedUri() {
        return false;
    }
}