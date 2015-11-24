package com.github.onsdigital.babbage.api.endpoint.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.endpoint.search.base.BaseSearchPage;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.template.TemplateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addTermFilter;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Api
public class Search extends BaseSearchPage {

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
    public BabbageResponse getPage(String requestedUri, HttpServletRequest request) throws IOException {
        String query = extractSearchTerm(request);
        if (isEmpty(query)) {
            return renderEmptyPage(request);
        } else {
            BabbageResponse response = getTimeSeriesResponse(query);
            if (response != null) {
                return response;
            } else {
                return super.getPage(cleanUri(request.getRequestURI()), request);
            }
        }
    }

    @Override
    public BabbageResponse getData(String requestedUri, HttpServletRequest request) throws IOException {
        String query = extractSearchTerm(request);
        if (isEmpty(query)) {
            return new BabbageStringResponse("Please enter search query", MediaType.TEXT_PLAIN);
        } else {
            BabbageResponse timeSeriesResponse = getTimeSeriesResponse(query);
            if (timeSeriesResponse != null) {
                return timeSeriesResponse;
            }
            return super.getData(requestedUri, request);
        }
    }


    private BabbageStringResponse renderEmptyPage(HttpServletRequest request) throws IOException {
        String html = TemplateService.getInstance().renderContent(getBaseData(request));
        return new BabbageStringResponse(html, TEXT_HTML);
    }

    /**
     * Searches against time series cdid for an exact match , if found creates a redirect response for the client
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

    @Override
    protected ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

}