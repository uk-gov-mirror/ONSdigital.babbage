package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.AggregateQuery;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addTermFilter;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Api
public class Search extends ListPageBaseRequestHandler {

    private final static ContentType[] ALLOWED_TYPES = {
            ContentType.bulletin,
            ContentType.article,
            ContentType.article_download,
            ContentType.compendium_landing_page,
            ContentType.timeseries,
            ContentType.dataset_landing_page,
            ContentType.reference_tables,
            ContentType.static_adhoc,
            ContentType.static_methodology,
            ContentType.static_methodology_download,
            ContentType.static_qmi,
            ContentType.static_article,
            ContentType.static_foi,
            ContentType.static_page,
            ContentType.static_landing_page
    };


    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = URIUtil.cleanUri(request.getRequestURI());
        String requestType = URIUtil.resolveRequestType(uri);
        if ("data".equals(requestType)) {
            getData(URIUtil.removeLastSegment(uri), request).apply(request, response);
        } else {
            getPage(request, response).apply(request, response);
        }
    }

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

    public BabbageResponse getPage(HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
        String query = extractSearchTerm(request);
        if (isEmpty(query)) {
            return renderEmptyPage(request);
        } else {
            BabbageResponse timeSeriesResponse = getTimeSeriesResponse(query);
            if (timeSeriesResponse != null) {
                return timeSeriesResponse;
            } else {
               return  super.get(cleanUri(request.getRequestURI()), request);
            }
        }
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

    private BabbageStringResponse renderEmptyPage(HttpServletRequest request) throws IOException {
        String html = TemplateService.getInstance().renderContent(getBaseData(request));
        return new BabbageStringResponse(html, TEXT_HTML);
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

    @Override
    protected LinkedHashMap<String, Object> prepareData(String requestedUri, HttpServletRequest request) throws IOException, ContentReadException {
        SearchResponseHelper contentResponse;
        SearchResponseHelper aggregateResponseHelper;
        SearchResponseHelper featuredResultsResponse = null;

        ONSQuery contentQuery = createQuery(requestedUri, request);
        AggregateQuery aggregateQuery = buildAggregateQuery(contentQuery);
        List<SearchResponseHelper> searchResponseHelpers;
        if (contentQuery.getPage() == 1 && !isFiltered(request)) {
             searchResponseHelpers = SearchService.getInstance().searchMultiple(contentQuery, aggregateQuery, buildFeaturedResultQuery(contentQuery.getSearchTerm()));
            featuredResultsResponse = searchResponseHelpers.get(2);
        } else {
            searchResponseHelpers = SearchService.getInstance().searchMultiple(contentQuery, aggregateQuery);
        }
        contentResponse = searchResponseHelpers.get(0);
        aggregateResponseHelper = searchResponseHelpers.get(1);

        Paginator.assertPage(contentQuery.getPage(), contentResponse);
        LinkedHashMap<String, Object> listData = new LinkedHashMap<>();
            listData.put("result", contentResponse.getResult());
            listData.put("counts", aggregateResponseHelper.getResult());
        if (featuredResultsResponse != null) {
            listData.put("featuredResult", featuredResultsResponse.getResult());
        }
        listData.put("paginator", Paginator.getPaginator(contentQuery.getPage(), contentResponse));
        listData.putAll(getBaseData(request));
        return listData;

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

    @Override
    protected boolean isAggregateByType() {
        return true;
    }
}