package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchRequestQueryBuilder;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addPrefixFilter;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addTermFilter;
import static com.github.onsdigital.babbage.util.common.EnumUtil.namesOf;

@Api
public class Search {

    public static final String CONTENT_TYPE = "text/html";
    private final static ContentType[] ALLOWED_TYPES = {
            ContentType.bulletin,
            ContentType.article,
            ContentType.data_slice,
            ContentType.compendium_landing_page,
            ContentType.static_article,
            ContentType.static_methodology,
            ContentType.static_qmi,
            ContentType.dataset_landing_page,
            ContentType.timeseries_dataset,
            ContentType.reference_tables
    };
    private final ContentType[] STATIC_TYPES = {ContentType.static_adhoc, ContentType.static_article, ContentType.static_foi, ContentType.static_page, ContentType.static_landing_page};

    private final String SEARCH_PAGE_TYPE = "search";

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentReadException, URISyntaxException {

        String type = URIUtil.resolveRequestType(request.getRequestURI());
        String query = SearchRequestHelper.extractSearchTerm(request);
        if (StringUtils.isEmpty(query)) {
            if (isDataRequest(type)) {
                return "Please enter a search query";
            } else {
                renderEmptyPage(response);
            }
        } else {
            search(request, response, query, type);
        }
        return null;
    }

    private void renderEmptyPage(HttpServletResponse response) throws IOException {
        LinkedHashMap<String, Object> searchData = new LinkedHashMap<>();
        searchData.put("type", SEARCH_PAGE_TYPE);
        String html = TemplateService.getInstance().renderListPage(searchData);
        BabbageResponse babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        babbageResponse.apply(response);
    }

    /*Searches for time series cdid, if there is a match redirects to time series page, otherwise performs search for content*/
    private void search(HttpServletRequest request, HttpServletResponse response, String query, String type) throws IOException {
        String uri = searchTimeSeriesUri(query);
        if (uri != null) {
            response.sendRedirect(uri);
            return;
        }
        LinkedHashMap<String, Object> searchData = searchContent(request, query);
        BabbageResponse searchResponse;
        if (isDataRequest(type)) {
            searchResponse = new BabbageStringResponse(JsonUtil.toJson(searchData));
        } else {
            searchData.put("type", SEARCH_PAGE_TYPE);
            String html = TemplateService.getInstance().renderListPage(searchData);
            searchResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        }
        searchResponse.apply(response);
    }

    private LinkedHashMap<String, Object> searchContent(HttpServletRequest request, String query) throws IOException {
        LinkedHashMap<String, Object> searchResponseData = new LinkedHashMap<>();
        int page = SearchRequestHelper.extractPage(request);

        boolean filtered = isFiltered(request);
        boolean notFiltered = (filtered == false);
        boolean timeSeriesRequested = isTimeSeriesRequested(request);
        boolean staticsRequested = isStaticsRequested(request);
        boolean countTimeSeries = page == 1 && (timeSeriesRequested || (notFiltered && timeSeriesRequested == false));
        boolean searchContent = staticsRequested || (notFiltered && timeSeriesRequested == false) || filtered;

        if (searchContent == false && page > 1) {
            throw new ResourceNotFoundException("Non-existing page request");
        }

        if (countTimeSeries) {
            ONSQuery featuredResultQuery = buildFeaturedResultQuery(query);
            SearchResponseHelper topicResponse = SearchService.getInstance().search(featuredResultQuery);
            if (topicResponse.getNumberOfResults() >= 1) {
                searchResponseData.put("featuredResult", topicResponse.getResult());
                //hide featured results if time series is requested
                if (timeSeriesRequested) {
                    searchResponseData.put("showFeaturedResult", false);
                } else {
                    searchResponseData.put("showFeaturedResult", true);
                }
                if (countTimeSeries) {
                    long timeSeriesCount = countTimeSeries((String) topicResponse.getResult().getResults().get(0).get(FilterableField.uri.name()));
                    searchResponseData.put("timeSeriesCount", timeSeriesCount);
                }
            }
        }

        if (searchContent) {
            ONSQuery contentQuery = buildContentQuery(request);
            SearchResponseHelper contentResponse = SearchService.getInstance().search(contentQuery);
            Paginator.assertPage(contentQuery.getPage(), contentResponse);
            searchResponseData.put("result", contentResponse.getResult());
            searchResponseData.put("paginator", Paginator.getPaginator(contentQuery.getPage(), contentResponse));
        }
        return searchResponseData;
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

    private ONSQuery buildContentQuery(HttpServletRequest request) throws IOException {
        ONSQuery query = new SearchRequestQueryBuilder(request, null, ALLOWED_TYPES).buildQuery();

        if(isFiltered(request) == false && isTimeSeriesRequested(request)) {
            query.setTypes(null);//clear types if time series requested
        }
        if (isStaticsRequested(request)) {
            String[] types = query.getTypes();
            query.setTypes(ArrayUtils.addAll(types, namesOf(STATIC_TYPES)));
        }
        return query;
    }

    private boolean isDataRequest(String type) {
        return "data".equals(type);
    }
}