package com.github.onsdigital.api.search;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.link.PageReference;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.base.PageType;
import com.github.onsdigital.content.page.search.SearchResultsPage;
import com.github.onsdigital.content.page.statistics.data.base.StatisticalData;
import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.page.taxonomy.ProductPage;
import com.github.onsdigital.content.util.ContentUtil;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;
import com.github.onsdigital.search.bean.AggregatedSearchResult;
import com.github.onsdigital.search.util.SearchHelper;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * Search endpoint that invokes appropriate search engine
 * //TODO: Tidy up, re-structure search and elastic search indexing keeping content library in mind.
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
            int page = extractPage(request);
            if (StringUtils.isNotBlank(request.getParameter("q"))) {
                String[] types = extractTypes(request);
                searchResult = search(query, page, types);
                if (searchResult == null) {
                    System.out.println("Attempting search against timeseries as no results found for: " + query);
                    URI timeseriesUri = searchTimseries(query, types);
                    if (timeseriesUri == null) {
                        System.out.println("No results found from timeseries so using suggestions for: " + query);
                        searchResult = searchAutocorrect(query, page, types);
                    } else {
                        response.sendRedirect(timeseriesUri.toString());
                        return null;
                    }
                }
            } /*else if (StringUtils.isNotBlank(request.getParameter("term"))) {
                searchResult = autoComplete(query);
            }*/ else if (StringUtils.isNotBlank(request.getParameter("cdid"))) {
                URI timeseriesUri = SearchHelper.searchCdid(query);
                return timeseriesUri == null ? "" : timeseriesUri;
            }

            handleResponse(type, searchResult, response, page, query);
            return null;
        } catch (Exception e) {
            ApiErrorHandler.handle(e, response);
            return null;
        }
    }


    //Decide if json should be returned ( in case search/data requested) or page should be rendered
    private void handleResponse(String requestType, Object searchResult, HttpServletResponse response, int page, String query) throws IOException {
        BabbageResponse babbageResponse;

        switch (requestType) {
            case DATA_REQUEST:
                babbageResponse = new BabbageStringResponse(ContentUtil.serialise(buildResultsPage((AggregatedSearchResult) searchResult, page, query)));
                break;
            case SEARCH_REQUEST:
                babbageResponse = new BabbageStringResponse(renderSearchPage((AggregatedSearchResult) searchResult, page, query), HTML_MIME);
                break;
            default:
                throw new ResourceNotFoundException();
        }
        babbageResponse.apply(response);
    }


    public String renderSearchPage(AggregatedSearchResult results, int currentPage, String searchTerm) throws IOException {
        SearchResultsPage searchPage = buildResultsPage(results, currentPage, searchTerm);
        searchPage.setNavigation(NavigationUtil.getNavigation());
        return TemplateService.getInstance().renderPage(searchPage);
    }

    //Resolve search headlines and build search page
    private SearchResultsPage buildResultsPage(AggregatedSearchResult results, int currentPage, String searchTerm) {
        SearchResultsPage page = new SearchResultsPage();
        page.setStatisticsSearchResult(results.statisticsSearchResult);
        page.setTaxonomySearchResult(results.taxonomySearchResult);
        page.setCurrentPage(currentPage);
        page.setNumberOfResults(results.getNumberOfResults());
        page.setNumberOfPages((long) Math.ceil(results.getNumberOfResults() / 10));
        page.setSearchTerm(searchTerm);
        page.setSuggestionBased(results.isSuggestionBasedResult());
        if (results.isSuggestionBasedResult()) {
            page.setSuggestion(results.getSuggestion());
        }

        if (page.getTaxonomySearchResult() != null) {
            resolveSearchHeadline(page);
        }
        return page;
    }

    private void resolveSearchHeadline(SearchResultsPage page) {
        for (Iterator<PageReference> iterator = page.getTaxonomySearchResult().getResults().iterator(); iterator.hasNext(); ) {
            PageReference pageReference = iterator.next();
            //Beware! Very messy code,
            // Elastic search does not contain whole data, so we have to load referenced data to get headline data reference (the first data item in the product page) and then data for that page
            //Afterwards reference needs updating back to data in elastic search to reduce data
            //Search in general needs tidying up. After going live hopefuly

            if (PageType.product_page == pageReference.getType()) {
                ContentUtil.loadReferencedPage(DataService.getInstance(), pageReference);
                ProductPage productPage = (ProductPage) pageReference.getData();;
                List<PageReference> items = productPage.getItems();
                if (items != null) {
                    if(items.size() > 0) {
                        PageReference headlineData = items.iterator().next();
                        if (headlineData != null) {
                            ContentUtil.loadReferencedPage(DataService.getInstance(), headlineData);
                            iterator.remove();
                            page.setHeadlinePage(productPage);
                            break;
                        }
                    }
                }
            }
        }
    }

    private Object search(String query, int page, String[] types) throws Exception {

        AggregatedSearchResult searchResult = SearchHelper.search(query, page, types);
        if (searchResult.getNumberOfResults() == 0 && types == null) {
            return null;
        }
        return searchResult;
    }

    private URI searchTimseries(String query, String[] types) {
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
