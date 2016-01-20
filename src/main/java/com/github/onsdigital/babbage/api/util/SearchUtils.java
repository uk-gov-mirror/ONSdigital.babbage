package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.helpers.ONSSearchResponse;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

import static com.github.onsdigital.babbage.api.util.ListUtils.getBaseListTemplate;
import static com.github.onsdigital.babbage.search.helpers.ONSQueryBuilders.*;
import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static com.github.onsdigital.babbage.search.model.field.Field.cdid;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by bren on 20/01/16.
 * <p/>
 * Commons search functionality for search, search publications and search data pages.
 */
public class SearchUtils {

    /**
     * Performs search for requested search term against filtered content types and counts contents types.
     * Content results are serialised into json with key "result" and document counts are serialised as "counts".
     * <p/>
     * Accepts extra searches to perform along with content search and document counts.
     *
     * @param request
     * @param defaultFilters
     * @param typesToCount
     * @return
     */
    public static BabbageResponse search(HttpServletRequest request, Set<TypeFilter> defaultFilters, ContentType[] typesToCount, String listType, String searchTerm, NamedSearch... additionalSearches) throws IOException {
        if (searchTerm == null) {
            return buildResponse(request, listType, null);
        } else {
            //search time series by cdid, redirect to time series page if found
            String timeSeriesUri = searchTimeSeriesUri(searchTerm);
            if (timeSeriesUri != null) {
                return new BabbageRedirectResponse(timeSeriesUri);
            }
            return buildResponse(request, listType, searchContent(request, defaultFilters, typesToCount, searchTerm, additionalSearches));
        }
    }

    private static LinkedHashMap<String, SearchResult> searchContent(HttpServletRequest request, Set<TypeFilter> defaultFilters, ContentType[] typesToCount, String searchTerm, NamedSearch[] additionalSearches) {
        Set<TypeFilter> selectedFilters = extractSelectedFilters(request, defaultFilters);
        ContentType[] selectedContentTypes = resolveContentTypes(selectedFilters);
        ArrayList<ONSQuery> searchList = new ArrayList<>();
        searchList.add(buildSearchQuery(request, searchTerm).types(selectedContentTypes));//content query
        searchList.add(docCountsQuery(contentQuery(searchTerm)).types(typesToCount).size(0));//type counts
        addAdditionalSearches(searchList, additionalSearches);
        return doSearch(searchList, additionalSearches);
    }

    /**
     * Builds search query by resolving search term, page and sort parameters
     *
     * @param request
     * @param searchTerm
     * @return ONSQuery, null if no search term given
     */
    private static ONSQuery buildSearchQuery(HttpServletRequest request, String searchTerm) {
        int page = extractPage(request);
        SortBy sortBy = extractSortBy(request, SortBy.relevance);
        return onsQuery(typeBoostedQuery(contentQuery(searchTerm)))
                .page(page)
                .sortBy(sortBy)
                .highlight(true);
    }

    static LinkedHashMap<String, SearchResult> doSearch(ArrayList<ONSQuery> searchList, NamedSearch... additionalSearches) {
        List<ONSSearchResponse> responseList = SearchHelper.searchMultiple(searchList);
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
        results.put("result", responseList.get(0).getResult());
        results.put("counts", responseList.get(1).getResult());
        for (int i = 2; i < responseList.size(); i++) {
            results.put(additionalSearches[i - 2].name, responseList.get(i).getResult());
        }
        return results;
    }

    private static void addAdditionalSearches(List<ONSQuery> searchList, NamedSearch... additionalSearches) {
        for (NamedSearch additionalSearch : additionalSearches) {
            searchList.add(additionalSearch.query);
        }
    }


    private static String searchTimeSeriesUri(String searchTerm) {
        ONSSearchResponse search = SearchHelper.
                search(onsQuery(boolQuery().filter(termQuery(cdid.fieldName(), searchTerm)), ContentType.timeseries)
                        .size(1).fetchFields(Field.uri));

        if (search.getNumberOfResults() == 0) {
            return null;
        }
        Map<String, Object> timeSeries = search.getResult().getResults().iterator().next();
        return (String) timeSeries.get(Field.uri.fieldName());
    }

    //Send result back to client
    private static BabbageResponse buildResponse(HttpServletRequest request, String listType, Map<String, SearchResult> results) throws IOException {
        if (isDataRequest(request.getRequestURI())) {
            return buildDataResponse(listType, results);
        } else {
            return buildPageResponse(listType, results);
        }
    }

    static BabbageResponse buildDataResponse(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = buildResults(listType, results);
        return new BabbageStringResponse(JsonUtil.toJson(data), MediaType.APPLICATION_JSON);
    }

    static BabbageResponse buildPageResponse(String listType, Map<String, SearchResult> results) throws IOException {
        LinkedHashMap<String, Object> data = buildResults(listType, results);
        return new BabbageStringResponse(TemplateService.getInstance().renderContent(data), MediaType.TEXT_HTML);
    }

    private static LinkedHashMap<String, Object> buildResults(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = getBaseListTemplate(listType);
        if (results != null) {
            for (Map.Entry<String, SearchResult> result : results.entrySet()) {
                data.put(result.getKey(), result.getValue());
            }
        }
        return data;
    }

    /**
     * Search names are serialised into json response with given names.
     */
    //Since templates are not order based back-end is refactored to fit into templates structure with result keys.
    //Refactoring templates would probably be a nightmare
    public static class NamedSearch {
        private String name;
        private ONSQuery query;

        public NamedSearch(String name, ONSQuery query) {
            this.name = name;
            this.query = query;
        }
    }


}
