package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.model.ContentType;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.Fields;
import com.github.onsdigital.babbage.search.helpers.SearchFields;
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
    private final static String[] ALLOWED_TYPES = new String[]{
            ContentType.bulletin.name(),
//            ContentType.timeseries.name(),
            ContentType.data_slice.name(),
            ContentType.compendium_landing_page.name(),
            ContentType.static_article.name(),
            ContentType.static_methodology.name(),
            ContentType.static_qmi.name(),
            ContentType.dataset.name(),
            ContentType.timeseries_dataset.name(),
            ContentType.reference_tables.name()
    };
    private final String[] STATIC_TYPES = new String[]{ContentType.static_adhoc.name(), ContentType.static_article.name(), ContentType.static_foi.name(), ContentType.static_page.name()};

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, ContentNotFoundException, ContentReadException, URISyntaxException {

        String type = URIUtil.resolveRequestType(request.getRequestURI());
        String query = extractQuery(request);
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

        ONSQueryBuilder featuredResultQuery = buildFeaturedResultQuery(query);
        SearchRequestHelper searchHelper = getSearchHelper(request);
        ONSQueryBuilder searchQuery = searchHelper.buildQuery();

        List<SearchResponseHelper> responseHelpers = SearchService.getInstance().searchMultiple(featuredResultQuery, searchQuery);
        Iterator<SearchResponseHelper> iterator = responseHelpers.iterator();
        SearchResponseHelper featuredResponseHelper = iterator.next();
        SearchResponseHelper searchResponseHelper = iterator.next();

        Paginator.assertPage(searchHelper.getPage(), searchResponseHelper);

        LinkedHashMap<String, Object> searchData = new LinkedHashMap<>();
        searchData.put("type", type);
        searchData.put("paginator", Paginator.getPaginator(searchHelper.getPage(), searchResponseHelper));
        searchData.put("featuredResult", featuredResponseHelper.getResult());
        String html = TemplateService.getInstance().renderListPage(searchResponseHelper.getResult(), searchData);
        BabbageResponse babbageResponse = new BabbageStringResponse(html, CONTENT_TYPE);
        babbageResponse.apply(response);
    }

    private String searchTimeSeries(String query) throws IOException {
        ONSQueryBuilder onsQueryBuilder = new ONSQueryBuilder(ContentType.timeseries.name())
                .addFilter(Fields.cdid.name(), query.toLowerCase()).
                        setSize(1);
        SearchResponseHelper searchResponseHelper = SearchService.getInstance().search(onsQueryBuilder);
        if (searchResponseHelper.getNumberOfResults() > 0) {
            Map<String, Object> timeSeries = searchResponseHelper.getResult().getResults().iterator().next();
            return (String) timeSeries.get(Fields.uri.name());
        }
        return null;
    }

    private ONSQueryBuilder buildFeaturedResultQuery(String query) throws IOException {
        return new ONSQueryBuilder(ContentType.product_page.name())
                .setSize(1)
                .setQuery(query)
                .setFields(SearchFields.values())
                .setHighLightFields(true);
    }

    private SearchRequestHelper getSearchHelper(HttpServletRequest request) throws IOException {
        String[] submittedTypes = request.getParameterValues("type");
        String includeStatics = request.getParameter("includeStatics");
        String methodology = request.getParameter("methodology");
        SearchRequestHelper searchRequestHelper = new SearchRequestHelper(request, null, ALLOWED_TYPES);
        searchRequestHelper.setFilterLatest(true);

        if (submittedTypes == null && methodology != null) {
            //clear types if methodology is set, todo: do not use serach request helper for search or create a common base for search and list
            searchRequestHelper.setTypes(null);
        }

        String[] types = new String[0];
        if (StringUtils.isNotEmpty(includeStatics)) {
            types = STATIC_TYPES;
        }
        if (StringUtils.isNotEmpty(methodology)) {
            types = ArrayUtils.addAll(types, ContentType.static_methodology.name(), ContentType.static_qmi.name());
        }
        types = ArrayUtils.addAll(types, searchRequestHelper.getTypes());
        searchRequestHelper.setTypes(types);
        return searchRequestHelper;
    }

    private String extractQuery(HttpServletRequest request) {
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