package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.search.bean.AggregatedSearchResult;
import com.github.onsdigital.search.util.SearchHelper;
import com.github.onsdigital.template.TemplateRenderer;
import com.github.onsdigital.template.TemplateService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Search endpoint that invokes appropriate search engine
 *
 * @author Bren
 */
@Api
public class Search {
    private final static String HTML_MIME = "text/html";
    private final static String DATA_REQUEST = "data";
    private final static String SEARCH_REQUEST = "search";

    @GET
    public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

        String type = URIUtil.resolveRequestType(request.getRequestURI());

        try {
            String query = extractQuery(request);
            Object searchResult = null;
            if (StringUtils.isNotBlank(request.getParameter("q"))) {
                int page = extractPage(request);
                String[] types = extractTypes(request);
                searchResult = search(query, page, types);
                if (searchResult == null) {
                    System.out.println("Attempting search against timeseries as no results found for: " + query);
                    TimeSeries timeSeries = searchTimseries(query, types);
                    if (timeSeries == null) {
                        System.out.println("No results found from timeseries so using suggestions for: " + query);
                        searchResult =  searchAutocorrect(query, page, types);
                    } else {
                        response.sendRedirect(timeSeries.getUri().toString());
                        return null;
                    }
                }
            } else if (StringUtils.isNotBlank(request.getParameter("term"))) {
                searchResult = autoComplete(query);
            } else if (StringUtils.isNotBlank(request.getParameter("cdid"))) {
                TimeSeries timeseries = SearchHelper.searchCdid(query);
                return timeseries == null ? "" : timeseries.getUri();
            }

            handleResponse(type, searchResult, response);
            return null;
        } catch (Exception e) {
            ApiErrorHandler.handle(e, response);
            return null;
        }
    }


    //Decide if json should be returned ( in case search/data requested) or page should be rendered
    private void handleResponse(String requestType, Object searchResult, HttpServletResponse response) throws IOException {
        BabbageResponse babbageResponse;

        switch (requestType) {
            case DATA_REQUEST:
                babbageResponse = new BabbageStringResponse(ContentUtil.serialise(searchResult));
                break;
            case SEARCH_REQUEST:
                babbageResponse = new BabbageStringResponse(renderSearchPage(searchResult), HTML_MIME);
                break;
            default:
                throw new ResourceNotFoundException();
        }
        babbageResponse.apply(response);
    }

    public String renderSearchPage(Object results ) throws IOException {
        return TemplateService.getInstance().render(results, Configuration.getSearchTemplateName());
    }

    private Object search(String query, int page, String[] types) throws Exception {

        AggregatedSearchResult searchResult = SearchHelper.search(query, page, types);
        if (searchResult.getNumberOfResults() == 0 && types == null) {
            return null;
        }
        return searchResult;
    }

    private TimeSeries searchTimseries(String query, String[] types) {
        return SearchHelper.searchCdid(query);
    }

    private Object searchAutocorrect(String query, int page, String[] types) throws Exception {
        AggregatedSearchResult suggestionResult = SearchHelper.searchSuggestions(query, page, types);
        return suggestionResult;
    }

    public Object autoComplete(String query) {
        return SearchHelper.autocomplete(query);
    }

    private int extractPage(HttpServletRequest request) {
        String page = request.getParameter("page");
        if (StringUtils.isNotEmpty(page) && StringUtils.isNumeric(page)) {
            int pageNumber = Integer.parseInt(page);
            return pageNumber < 1 ? 1 : pageNumber;
        }
        return 1;
    }

    private String[] extractTypes(HttpServletRequest request) {
        String[] types = request.getParameterValues("type");
        return ArrayUtils.isNotEmpty(types) ? types : null;
    }

    private String extractQuery(HttpServletRequest request) {
        String query = request.getParameter("q");

        if (StringUtils.isEmpty(query)) {
            // check to see if this is part of search's autocomplete
            query = request.getParameter("term");
            if (StringUtils.isEmpty(query)) {
                query = request.getParameter("cdid");
                if (StringUtils.isEmpty(query)) {
                    throw new IllegalArgumentException("No search query provided");
                }
            }
        }
        if (query.length() > 100) {
            throw new IllegalArgumentException("Search query contains too many characters");
        }
        String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9 ]+", "");

        return sanitizedQuery;
    }

}
