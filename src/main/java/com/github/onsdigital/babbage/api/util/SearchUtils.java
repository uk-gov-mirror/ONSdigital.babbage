package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.ONSSearchResponse;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getSearchResponseCacheTime;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.advancedSearchQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.contentQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.departmentQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.listQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.onsQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.typeBoostedQuery;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPage;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSelectedFilters;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSortBy;
import static com.github.onsdigital.babbage.search.input.TypeFilter.contentTypes;
import static com.github.onsdigital.babbage.search.model.field.Field.cdid;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.suggest.SuggestBuilders.phraseSuggestion;

/**
 * Created by bren on 20/01/16.
 * <p>
 * Commons search functionality for search, search publications and search data pages.
 */
public class SearchUtils {

    private static final String DEPARTMENTS_INDEX = "departments";

    /**
     * Performs search for requested search term against filtered content types and counts contents types.
     * Content results are serialised into json with key "result" and document counts are serialised as "counts".
     * <p/>
     * Accepts extra searches to perform along with content search and document counts.
     *
     * @param request
     * @return
     */
    public static BabbageResponse search(HttpServletRequest request, String listType, String searchTerm, SearchQueries queries, boolean searchDepartments) throws IOException {
        if (searchTerm == null) {
            return buildResponse(request, listType, null);
        } else if(!isFiltered(request)) { //only search for time series when new search made through search input
            //search time series by cdid, redirect to time series page if found
            String timeSeriesUri = searchTimeSeriesUri(searchTerm);
            if (timeSeriesUri != null) {
                return new BabbageRedirectResponse(timeSeriesUri, Configuration.GENERAL.getSearchResponseCacheTime());
            }
        }
        LinkedHashMap<String, SearchResult> results = searchAll(queries);
        if (searchDepartments) {
            searchDeparments(searchTerm, results);
        }
        return buildResponse(request, listType, results);
    }

    public static BabbageResponse list(HttpServletRequest request, String listType, SearchQueries queries) throws IOException {
        return buildResponse(request, listType, searchAll(queries));
    }

    public static BabbageResponse listPage(String listType, SearchQueries queries) throws IOException {
        return buildPageResponse(listType, searchAll(queries));
    }

    public static BabbageResponse listJson(String listType, SearchQueries queries) throws IOException {
        return buildDataResponse(listType, searchAll(queries));
    }

    public static LinkedHashMap<String, SearchResult> searchAll(SearchQueries searchQueries) {
        List<ONSQuery> queries = searchQueries.buildQueries();
        return doSearch(queries);
    }

    /**
     * Builds search query by resolving search term, page and sort parameters
     *
     * @param request
     * @param searchTerm
     * @return ONSQuery, null if no search term given
     */
    public static ONSQuery buildSearchQuery(HttpServletRequest request, String searchTerm, Set<TypeFilter> defaultFilters) {
        boolean advanced = isAdvancedSearchQuery(searchTerm);
        SortBy sortBy = extractSortBy(request, SortBy.relevance);
        QueryBuilder contentQuery;
        contentQuery = advanced ? advancedSearchQuery(searchTerm) : contentQuery(searchTerm);
        ONSQuery query = buildONSQuery(request, contentQuery, sortBy, null, contentTypes(extractSelectedFilters(request, defaultFilters)));
        if (!advanced) {
            query.suggest(phraseSuggestion("search_suggest").field(Field.title_no_synonym_no_stem.fieldName()).text(searchTerm));
        }
        return query;
    }

    private static boolean isAdvancedSearchQuery(String searchTerm) {
        return StringUtils.containsAny(searchTerm, "+", "|", "-", "\"", "*", "~");
    }

    /**
     * Advanced search query corresponds to elastic search simple query string query, allowing user to control search results using special characters (+ for AND, | for OR etc)
     *
     * @return
     */
    public static ONSQuery buildAdvancedSearchQuery(HttpServletRequest request, String searchTerm, Set<TypeFilter> defaultFilters) {
        SortBy sortBy = extractSortBy(request, SortBy.relevance);
        ONSQuery query = buildONSQuery(request, advancedSearchQuery(searchTerm), sortBy, null, contentTypes(extractSelectedFilters(request, defaultFilters)));
        return query;
    }
    public static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters, SearchFilter filter) {
        return buildListQuery(request, defaultFilters, filter, null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, SearchFilter filter) {
        return buildListQuery(request, null, filter, null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, SearchFilter filter, SortBy defaultSort) {
        return buildListQuery(request, null, filter, defaultSort);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request) {
        return buildListQuery(request, null, null, null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters) {
        return buildListQuery(request, defaultFilters, null, null);
    }


    private static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters, SearchFilter filter, SortBy defaultSort) {
        String searchTerm = extractSearchTerm(request);
        boolean hasSearchTerm = isNotEmpty(searchTerm);
        SortBy sortBy;
        if (hasSearchTerm) {
            sortBy = SortBy.relevance;
        } else {
            sortBy = defaultSort == null ? SortBy.release_date : defaultSort;
        }

        QueryBuilder query = buildBaseListQuery(searchTerm);
        ContentType[] contentTypes = defaultFilters == null ? null : contentTypes(extractSelectedFilters(request, defaultFilters));
        return buildONSQuery(request, query, sortBy, filter, contentTypes);
    }

    public static QueryBuilder buildBaseListQuery(String searchTerm) {
        QueryBuilder query;
        if (isNotEmpty(searchTerm)) {
            query = listQuery(searchTerm);
        } else {
            query = matchAllQuery();
        }
        return query;
    }

    private static ONSQuery buildONSQuery(HttpServletRequest request, QueryBuilder builder, SortBy defaultSort, SearchFilter filter, ContentType... contentTypes) {
        int page = extractPage(request);
        SortBy sort = extractSortBy(request, defaultSort);
        return onsQuery(typeBoostedQuery(builder), filter)
                .types(contentTypes)
                .page(page)
                .sortBy(sort)
                .name("result")
                .size(extractSize(request))
                .highlight(true);
    }

    /**
     * If a size parameter exists use that otherwise use default.
     */
    private static int extractSize(HttpServletRequest request) {
        int result = getResultsPerPage();
        if (StringUtils.isNotEmpty(request.getParameter("size"))) {
            try {
                result = Integer.parseInt(request.getParameter("size"));
                return Math.max(getResultsPerPage(), Math.min(result, getMaxResultsPerPage()));
            } catch (NumberFormatException ex) {
                System.out.println(MessageFormat.format("Failed to parse size parameter to integer." +
                        " Default value will be used.\n {0}", ex));
            }
        }
        return result;
    }

    static LinkedHashMap<String, SearchResult> doSearch(List<ONSQuery> searchQueries) {
        List<ONSSearchResponse> responseList = SearchHelper.searchMultiple(searchQueries);
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
        for (int i = 0; i < responseList.size(); i++) {
            ONSSearchResponse response = responseList.get(i);
            results.put(searchQueries.get(i).name(), response.getResult());

        }
        return results;
    }

    private static String searchTimeSeriesUri(String searchTerm) {
        ONSSearchResponse search = SearchHelper.
                search(onsQuery(boolQuery().filter(termQuery(cdid.fieldName(), searchTerm)))
                        .types(ContentType.timeseries)
                        .size(1)
                        .fetchFields(Field.uri));
        if (search.getNumberOfResults() == 0) {
            return null;
        }
        Map<String, Object> timeSeries = search.getResult().getResults().iterator().next();
        return (String) timeSeries.get(Field.uri.fieldName());
    }

    private static void searchDeparments(String searchTerm, LinkedHashMap<String, SearchResult> results) {
        QueryBuilder departmentsQuery = departmentQuery(searchTerm);
        SearchRequestBuilder departmentsSearch = ElasticSearchClient.getElasticsearchClient().prepareSearch(DEPARTMENTS_INDEX);
        departmentsSearch.setQuery(departmentsQuery);
        departmentsSearch.setSize(1);
        departmentsSearch.addHighlightedField("terms", 0, 0);
        departmentsSearch.setHighlighterPreTags("<strong>");
        departmentsSearch.setHighlighterPostTags("</strong>");
        SearchResponse response = departmentsSearch.get();
        ONSSearchResponse onsSearchResponse = new ONSSearchResponse(response);
        if (onsSearchResponse.getNumberOfResults() == 0) {
            return;
        }
        Map<String, Object> hit = onsSearchResponse.getResult().getResults().get(0);
        Text[] highlightedFragments = response.getHits().getAt(0).getHighlightFields().get("terms").getFragments();
        if (highlightedFragments != null && highlightedFragments.length > 0) {
            hit.put("match", highlightedFragments[0].toString());
        }
        results.put("departments", onsSearchResponse.getResult());
    }

    //Send result back to client
    public static BabbageResponse buildResponse(HttpServletRequest request, String listType, Map<String, SearchResult> results) throws IOException {
        if (isDataRequest(request.getRequestURI())) {
            return buildDataResponse(listType, results);
        } else {
            return buildPageResponse(listType, results);
        }
    }

    public static BabbageResponse buildDataResponse(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = buildResults(listType, results);
        return new BabbageStringResponse(JsonUtil.toJson(data), MediaType.APPLICATION_JSON, getSearchResponseCacheTime());
    }

    public static BabbageResponse buildPageResponse(String listType, Map<String, SearchResult> results) throws IOException {
        LinkedHashMap<String, Object> data = buildResults(listType, results);
        return new BabbageStringResponse(TemplateService.getInstance().renderContent(data), MediaType.TEXT_HTML, getSearchResponseCacheTime());
    }

    public static LinkedHashMap<String, Object> buildResults(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = getBaseListTemplate(listType);
        if (results != null) {
            for (Map.Entry<String, SearchResult> result : results.entrySet()) {
                data.put(result.getKey(), result.getValue());
            }
        }
        return data;
    }

    private static boolean isFiltered(HttpServletRequest request) {
        String[] filter = request.getParameterValues("filter");
        if (extractPage(request) == 1 && isEmpty(filter)) {
            return false;
        }
        return true;
    }

    private static LinkedHashMap<String, Object> getBaseListTemplate(String listType) {
        LinkedHashMap<String, Object> baseData = new LinkedHashMap<>();
        baseData.put("type", "list");
        baseData.put("listType", listType.toLowerCase());
        baseData.put("uri", ((RequestUtil.Location) ThreadContext.getData("location")).getPathname());
        return baseData;
    }


}
