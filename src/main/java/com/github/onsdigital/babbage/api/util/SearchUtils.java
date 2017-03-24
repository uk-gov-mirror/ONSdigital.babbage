package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.error.ValidationError;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.builders.ONSFilterBuilders;
import com.github.onsdigital.babbage.search.builders.ONSQueryBuilders;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.ONSSearchResponse;
import com.github.onsdigital.babbage.search.helpers.SearchHelper;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.*;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.ListUtil;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.*;

import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractPage;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSearchTerm;
import static com.github.onsdigital.babbage.api.util.HttpRequestUtil.extractSortBy;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getSearchResponseCacheTime;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.*;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSelectedFilters;
import static com.github.onsdigital.babbage.search.input.TypeFilter.contentTypes;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.suggest.SuggestBuilders.phraseSuggestion;

/**
 * Created by bren on 20/01/16.
 * <p>
 * Commons search functionality for search, search publications and search data pages.
 */
public class SearchUtils {
    private static final String TIMESERIES_PATH = "timeseries/%1$s";
    private static final String SEARCH_SERVICE_SCHEME = "http";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchUtils.class);
    private static final String ERRORS_KEY = "errors";
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    public static SearchResults search(final SearchParam searchParam) throws IOException, URISyntaxException {
        //If it is a Timeseries return

        final URIBuilder uriBuilder = new URIBuilder().setScheme(SEARCH_SERVICE_SCHEME)
                                                      .setHost("localhost")
                                                      .setPort(10001)
                                                      .setPath("search")
                                                      .addParameter("size",
                                                                    Integer.toString(searchParam.getSize()))
                                                      .addParameter("from",
                                                                    Integer.toString(from(searchParam.getPage(),
                                                                                          searchParam.getSize())))
                                                      .addParameter("term",
                                                                    searchParam.getSearchTerm());

        if (null != searchParam.getSortBy()) {
            uriBuilder.addParameter("sort",
                                    searchParam.getSortBy()
                                               .name());
        }

        if (StringUtils.isNotBlank(searchParam.getAggregationField())) {
            uriBuilder.addParameter("aggField",
                                    searchParam.getAggregationField());
        }

        if (null != searchParam.getFilters()) {
            searchParam.getFilters()
                       .forEach(f -> uriBuilder.addParameter(f.getKey(),
                                                             f.getValue()));

        }

        if (null != searchParam.getDocTypes()) {
            for (ContentType s : searchParam.getDocTypes()) {
                uriBuilder.addParameter("type",
                                        s.name());
            }
        }

        if (ListUtil.isNotEmpty(searchParam.getQueryTypes())) {
            searchParam.getQueryTypes()
                       .forEach(s -> uriBuilder.addParameter("query",
                                                             s.name()
                                                              .toLowerCase()));
        }

        final URI searchUri = uriBuilder.build();

        byte[] responseBytes = executeGet(searchUri);

        return SearchResultsFactory.getInstance(responseBytes,
                                                searchParam.getSortBy(),
                                                searchParam.getPage(),
                                                searchParam.getSize(),
                                                searchParam.getQueryTypes());

    }

    private static byte[] executeGet(final URI searchUri) throws IOException {
        HttpGet httpget = new HttpGet(searchUri);

        byte[] responseBytes;

        try (final CloseableHttpResponse execute = httpClient.execute(httpget)) {
            final HttpEntity entity = execute.getEntity();
            try (final InputStream content = entity.getContent()) {
                responseBytes = IOUtils.toByteArray(content);
            }
        }
        return responseBytes;
    }


    /**
     * Performs search for requested search term against filtered content types and counts contents types.
     * Content results are serialised into json with key "result" and document counts are serialised as "counts".
     * <p>
     * Accepts extra searches to perform along with content search and document counts.
     *
     * @param
     * @param
     * @return
     */
    public static BabbageResponse search(boolean isDataRequest,
                                         String listType,
                                         SearchParam searchParam) throws IOException, URISyntaxException {

        final TimeSeriesResult ts = searchTimeSeriesUri(searchParam.getSearchTerm());
        final BabbageResponse babbageResponse;
        if (null != ts) {
            babbageResponse = new BabbageRedirectResponse(ts.getUri(),
                                                          Configuration.GENERAL.getSearchResponseCacheTime());
        }
        else {
            final SearchResults search = search(searchParam);

            final Map<String, SearchResult> results = new HashMap<>();
            search.getResults()
                  .forEach(sr -> results.put(sr.getQueryType()
                                               .getText(), sr));


            // TODO Put logging back
//        logResponseStatistics(searchTerm,
//                              queries,
//                              results);

            babbageResponse = buildResponse(isDataRequest,
                                            listType,
                                            results);
        }
        return babbageResponse;
    }

    /**
     * Performs search for requested search term against filtered content types and counts contents types.
     * Content results are serialised into json with key "result" and document counts are serialised as "counts".
     * <p>
     * Accepts extra searches to perform along with content search and document counts.
     *
     * @param
     * @return
     */

    private static void logResponseStatistics(String searchTerm, SearchQueries queries,
                                              LinkedHashMap<String, SearchResult> results) {


        for (ONSQuery onsQuery : queries.buildQueries()) {

            final int size = onsQuery.size();
            final Integer page = onsQuery.page();
            final String name = onsQuery.name();

            SearchResult resultsQueryResponse = results.get(name);
            long took = resultsQueryResponse.getTook();

            LOGGER.info("doSearch([searchQueries]) : name '{}' page '{}' took:'{}' ms for terms: '{}' size {}",
                        name,
                        page,
                        took,
                        searchTerm,
                        size
                       );
        }
    }


    public static BabbageResponse list(boolean isData, String listType,
                                       SearchQueries queries) throws IOException {
        return buildResponse(isData,
                             listType,
                             searchAll(queries));
    }

    public static BabbageResponse listPage(String listType, SearchQueries queries) throws IOException {
        return buildPageResponse(listType,
                                 searchAll(queries));
    }

    public static BabbageResponse listPageWithValidationErrors(
            String listType, SearchQueries queries,
            List<ValidationError> errors) throws IOException {
        return buildPageResponseWithValidationErrors(listType,
                                                     searchAll(queries),
                                                     Optional.ofNullable(errors));
    }

    public static BabbageResponse listJson(String listType, SearchQueries queries) throws IOException {
        return buildDataResponse(listType,
                                 searchAll(queries));
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
    public static ONSQuery buildSearchQuery(HttpServletRequest request, String searchTerm,
                                            Set<TypeFilter> defaultFilters) {
        boolean advanced = isAdvancedSearchQuery(searchTerm);
        SortBy sortBy = extractSortBy(request,
                                      SortBy.relevance);
        QueryBuilder contentQuery;
        contentQuery = advanced ? advancedSearchQuery(searchTerm) : contentQuery(searchTerm);
        ONSQuery query = buildONSQuery(request,
                                       contentQuery,
                                       sortBy,
                                       null,
                                       contentTypes(extractSelectedFilters(request,
                                                                           defaultFilters)));
        if (!advanced) {
            query.suggest(phraseSuggestion("search_suggest").field(Field.title_no_synonym_no_stem.fieldName())
                                                            .text(searchTerm));
        }
        return query;
    }

    private static boolean isAdvancedSearchQuery(String searchTerm) {
        return StringUtils.containsAny(searchTerm,
                                       "+",
                                       "|",
                                       "-",
                                       "\"",
                                       "*",
                                       "~");
    }

    /**
     * Advanced search query corresponds to elastic search simple query string query, allowing user to control search results using special characters (+ for AND, | for OR etc)
     *
     * @return
     */
    public static ONSQuery buildAdvancedSearchQuery(HttpServletRequest request, String searchTerm,
                                                    Set<TypeFilter> defaultFilters) {
        SortBy sortBy = extractSortBy(request,
                                      SortBy.relevance);
        return buildONSQuery(request,
                             advancedSearchQuery(searchTerm),
                             sortBy,
                             null,
                             contentTypes(extractSelectedFilters(request,
                                                                 defaultFilters)));
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters,
                                          SearchFilter filter) {
        return buildListQuery(request,
                              defaultFilters,
                              filter,
                              null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, SearchFilter filter) {
        return buildListQuery(request,
                              null,
                              filter,
                              null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, SearchFilter filter, SortBy defaultSort) {
        return buildListQuery(request,
                              null,
                              filter,
                              defaultSort);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request) {
        return buildListQuery(request,
                              null,
                              null,
                              null);
    }

    public static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters) {
        return buildListQuery(request,
                              defaultFilters,
                              null,
                              null);
    }


    private static ONSQuery buildListQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters,
                                           SearchFilter filter, SortBy defaultSort) {
        String searchTerm = extractSearchTerm(request);
        boolean hasSearchTerm = isNotEmpty(searchTerm);
        SortBy sortBy;
        if (hasSearchTerm) {
            sortBy = SortBy.relevance;
        }
        else {
            sortBy = defaultSort == null ? SortBy.release_date : defaultSort;
        }

        QueryBuilder query = buildBaseListQuery(searchTerm);
        ContentType[] contentTypes = defaultFilters == null ? null : contentTypes(extractSelectedFilters(request,
                                                                                                         defaultFilters));
        return buildONSQuery(request,
                             query,
                             sortBy,
                             filter,
                             contentTypes);
    }

    private static QueryBuilder buildBaseListQuery(String searchTerm) {
        QueryBuilder query;
        if (isNotEmpty(searchTerm)) {
            query = listQuery(searchTerm);
        }
        else {
            query = matchAllQuery();
        }
        return query;
    }

    private static int from(Integer page, Integer size) {
        int from = 0;
        //Default size to elastic default of 10
        size = (null != size ? size : 10);

        if (null != page && page > 1) {
            from = (page - 1) * size;
        }

        return from;

    }

    private static ONSQuery buildONSQuery(HttpServletRequest request, QueryBuilder builder, SortBy defaultSort,
                                          SearchFilter filter, ContentType... contentTypes) {
        int page = extractPage(request);
        SortBy sort = extractSortBy(request,
                                    defaultSort);
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
    public static int extractSize(HttpServletRequest request) {
        int result = getResultsPerPage();
        if (StringUtils.isNotEmpty(request.getParameter("size"))) {
            try {
                result = Integer.parseInt(request.getParameter("size"));
                return Math.max(getResultsPerPage(),
                                Math.min(result,
                                         getMaxResultsPerPage()));
            }
            catch (NumberFormatException ex) {
                System.out.println(MessageFormat.format("Failed to parse size parameter to integer." +
                                                                " Default value will be used.\n {0}",
                                                        ex));
            }
        }
        return result;
    }

    private static LinkedHashMap<String, SearchResult> doSearch(List<ONSQuery> searchQueries) {
        List<ONSSearchResponse> responseList = SearchHelper.searchMultiple(searchQueries);
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
        for (int i = 0; i < responseList.size(); i++) {
            ONSSearchResponse response = responseList.get(i);
            results.put(searchQueries.get(i)
                                     .name(),
                        response.getResult());


        }

        return results;
    }

    private static TimeSeriesResult searchTimeSeriesUri(String searchTerm) throws URISyntaxException, IOException {


        final URI timeSeriesUri = new URIBuilder().setScheme(SEARCH_SERVICE_SCHEME)
                                                  .setHost("localhost")
                                                  .setPort(10001)
                                                  .setPath(String.format(TIMESERIES_PATH,
                                                                         searchTerm))
                                                  .build();

        byte[] responseBytes = executeGet(timeSeriesUri);

        return TimeSeriesResultFactory.getInstance(responseBytes);
    }


    //Send result back to client
    public static BabbageResponse buildResponse(boolean isData, String listType,
                                                Map<String, SearchResult> results) throws IOException {
        if (isData) {
            return buildDataResponse(listType,
                                     results);
        }
        else {
            return buildPageResponse(listType,
                                     results);
        }
    }

    public static BabbageResponse buildDataResponse(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = buildResults(listType,
                                                          results);
        return new BabbageStringResponse(JsonUtil.toJson(data),
                                         MediaType.APPLICATION_JSON,
                                         getSearchResponseCacheTime());
    }

    public static BabbageResponse buildPageResponse(String listType,
                                                    Map<String, SearchResult> results) throws IOException {
        LinkedHashMap<String, Object> data = buildResults(listType,
                                                          results);
        return new BabbageStringResponse(TemplateService.getInstance()
                                                        .renderContent(data),
                                         MediaType.TEXT_HTML,
                                         getSearchResponseCacheTime());
    }


    private static BabbageResponse buildPageResponseWithValidationErrors(
            String
                    listType, Map<String, SearchResult>
                    results, Optional<List<ValidationError>> errors) throws IOException {
        LinkedHashMap<String, Object> data = buildResults(listType,
                                                          results);
        if (errors.isPresent() && !errors.get()
                                         .isEmpty()) {
            data.put(ERRORS_KEY,
                     errors.get());
        }
        return new BabbageStringResponse(TemplateService.getInstance()
                                                        .renderContent(data),
                                         MediaType.TEXT_HTML,
                                         getSearchResponseCacheTime());
    }

    public static LinkedHashMap<String, Object> buildResults(String listType, Map<String, SearchResult> results) {
        LinkedHashMap<String, Object> data = getBaseListTemplate(listType);
        if (results != null) {
            for (Map.Entry<String, SearchResult> result : results.entrySet()) {
                data.put(result.getKey(),
                         result.getValue());
            }
        }
        return data;
    }

    /**
     * search time series for a given uri without dealing with request / response objects.
     *
     * @param uriString
     * @return
     */
    public static HashMap<String, SearchResult> searchTimeseriesForUri(String uriString) {
        QueryBuilder builder = QueryBuilders.matchAllQuery();
        SortBy sortByReleaseDate = SortBy.release_date;

        SearchFilter filter = boolQueryBuilder -> {
            if (isNotEmpty(uriString)) {
                ONSFilterBuilders.filterUriPrefix(uriString,
                                                  boolQueryBuilder);
            }
        };

        ONSQuery query = onsQuery(typeBoostedQuery(builder),
                                  filter)
                .types(ContentType.timeseries)
                .sortBy(sortByReleaseDate)
                .name("result")
                .highlight(true);

        SearchQueries queries = () -> ONSQueryBuilders.toList(query);
        return SearchUtils.searchAll(queries);
    }

    private static LinkedHashMap<String, Object> getBaseListTemplate(String listType) {
        LinkedHashMap<String, Object> baseData = new LinkedHashMap<>();
        baseData.put("type",
                     "list");
        baseData.put("listType",
                     listType.toLowerCase());
        baseData.put("uri",
                     ((RequestUtil.Location) ThreadContext.getData("location")).getPathname());
        return baseData;
    }


}
