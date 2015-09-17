package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.util.URIUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            ContentType.dataset,
            ContentType.timeseries_dataset,
            ContentType.reference_tables
    };
    private final ContentType[] STATIC_TYPES = {ContentType.static_adhoc, ContentType.static_article, ContentType.static_foi, ContentType.static_page};

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentNotFoundException, ContentReadException, URISyntaxException {

        String type = URIUtil.resolveRequestType(request.getRequestURI());
        String query = extractSearchTerm(request);
        if (query == null) {
            renderEmptyPage(response, type);
        } else {
            search(request, response, query, type);
        }
        return null;
    }

    private void renderEmptyPage(HttpServletResponse response, String type) throws IOException {
        LinkedHashMap<String, Object> searchData = new LinkedHashMap<>();
        searchData.put("type", type);
        String html = TemplateService.getInstance().renderListPage(searchData);
        BabbageResponse babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        babbageResponse.apply(response);
    }

    private void search(HttpServletRequest request, HttpServletResponse response, String query, String type) throws IOException {
        String uri = searchTimeSeries(query);
        if (uri != null) {
            response.sendRedirect(uri);
            return;
        }
        searchContent(request, response, query, type);
    }

    private void searchContent(HttpServletRequest request, HttpServletResponse response, String query, String type) throws IOException {
        ONSQuery featuredResultQuery = buildFeaturedResultQuery(query);
        ONSQuery contentQuery = buildContentQuery(request);

        List<SearchResponseHelper> responseHelpers = SearchService.getInstance().searchMultiple(featuredResultQuery, contentQuery);
        Iterator<SearchResponseHelper> iterator = responseHelpers.iterator();
        SearchResponseHelper featuredResponseHelper = iterator.next();
        SearchResponseHelper searchResponseHelper = iterator.next();

        Paginator.assertPage(contentQuery.getPage(), searchResponseHelper);

        LinkedHashMap<String, Object> searchData = new LinkedHashMap<>();
        searchData.put("type", type);
        searchData.put("paginator", Paginator.getPaginator(contentQuery.getPage(), searchResponseHelper));
        searchData.put("featuredResult", featuredResponseHelper.getResult());
        String html = TemplateService.getInstance().renderListPage(searchResponseHelper.getResult(), searchData);
        BabbageResponse babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        babbageResponse.apply(response);
    }

    private String searchTimeSeries(String query) throws IOException {
        ONSQuery onsQuery = new ONSQuery(ContentType.timeseries)
                .addFilter(FilterableField.cdid, query.toLowerCase()).
                        setSize(1);
        SearchResponseHelper searchResponseHelper = SearchService.getInstance().search(onsQuery);
        if (searchResponseHelper.getNumberOfResults() > 0) {
            Map<String, Object> timeSeries = searchResponseHelper.getResult().getResults().iterator().next();
            return (String) timeSeries.get(FilterableField.uri.name());
        }
        return null;
    }

    private ONSQuery buildFeaturedResultQuery(String query) throws IOException {
        return new ONSQuery(ContentType.product_page)
                .setSize(1)
                .setQuery(query)
                .setFields(SearchableField.values())
                .setHighLightFields(true);
    }

    private ONSQuery buildContentQuery(HttpServletRequest request) throws IOException {
        String includeStatics = request.getParameter("includeStatics");
        ONSQuery query = new SearchRequestHelper(request, null, ALLOWED_TYPES).buildQuery();

        if (StringUtils.isNotEmpty(includeStatics)) {
            ContentType[] types = query.getTypes();
            query.setTypes(ArrayUtils.addAll(types, STATIC_TYPES));
        }
        return query;
    }

    private String extractSearchTerm(HttpServletRequest request) {
        String query = request.getParameter("q");

        if (StringUtils.isEmpty(query)) {
            return null;
        }
        if (query.length() > 200) {
            throw new RuntimeException("Search query contains too many characters");
        }
        String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9 ]+", "");
        return sanitizedQuery;
    }
}