package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
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
import org.elasticsearch.index.query.QueryBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getSearchResponseCacheTime;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.*;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static com.github.onsdigital.babbage.search.input.TypeFilter.contentTypes;
import static com.github.onsdigital.babbage.search.model.field.Field.cdid;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.*;

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
     * @return
     */
    public static BabbageResponse search(HttpServletRequest request, String listType, String searchTerm, SearchQueries queries) throws IOException {
        if (searchTerm == null) {
            return buildResponse(request, listType, null);
        } else {
            //search time series by cdid, redirect to time series page if found
            String timeSeriesUri = searchTimeSeriesUri(searchTerm);
            if (timeSeriesUri != null) {
                return new BabbageRedirectResponse(timeSeriesUri, Configuration.GENERAL.getSearchResponseCacheTime());
            }
            return buildResponse(request, listType, searchAll(queries));
        }
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
        SortBy sortBy = extractSortBy(request, SortBy.relevance);
        return buildONSQuery(request, contentQuery(searchTerm), sortBy, null, contentTypes(extractSelectedFilters(request, defaultFilters)));
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
        return buildListQuery(request, null, null,null);
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

    private static QueryBuilder buildBaseListQuery(String searchTerm) {
        QueryBuilder query;
        if (isNotEmpty(searchTerm)) {
            query = contentQuery(searchTerm);
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
                .highlight(true);
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

    private static LinkedHashMap<String, Object> buildResults(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = getBaseListTemplate(listType);
        if (results != null) {
            for (Map.Entry<String, SearchResult> result : results.entrySet()) {
                data.put(result.getKey(), result.getValue());
            }
        }
        return data;
    }

    private static LinkedHashMap<String, Object> getBaseListTemplate(String listType) {
        LinkedHashMap<String, Object> baseData = new LinkedHashMap<>();
        baseData.put("type", "list");
        baseData.put("listType", listType.toLowerCase());
        baseData.put("uri", ((RequestUtil.Location) ThreadContext.getData("location")).getPathname());
        return baseData;
    }


}
