package com.github.onsdigital.babbage.api.util;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.error.ValidationError;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getResultsPerPage;
import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getSearchResponseCacheTime;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by bren on 20/01/16.
 * <p>
 * Commons search functionality for search, search publications and search data pages.
 */
public class SearchUtils {
    private final static DateFormat ISO_DATE_FORMAT = new ISO8601DateFormat();
    private static final String TIMESERIES_PATH = "timeseries/%1$s";
    private static final String SEARCH_SERVICE_SCHEME = "http";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchUtils.class);
    private static final String ERRORS_KEY = "errors";
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static SearchResults query(final SearchParam searchParam) throws IOException, URISyntaxException {
        //If it is a Timeseries return

        final URIBuilder uriBuilder = new URIBuilder().setScheme(SEARCH_SERVICE_SCHEME)
                                                      .setHost("localhost")
                                                      .setPort(10001)
                                                      .setPath("search");

        //TODO Externalise Keys?
        addParam(uriBuilder, "size", Integer.toString(searchParam.getSize()));
        addParam(uriBuilder, "from", Integer.toString(from(searchParam.getPage(), searchParam.getSize())));
        addParam(uriBuilder, "term", searchParam.getSearchTerm());
        addParam(uriBuilder, "highlight", searchParam.isHighlights());
        addParam(uriBuilder, "latest", searchParam.isLatest());
        final String uri = searchParam.getPrefixURI();
        if (uri != null) {
            addParam(uriBuilder, "uriPrefix", endsWith(uri, "/") ? uri : uri + "/");
        } else {
            addParam(uriBuilder, "uriPrefix", "/");
        }
        addParam(uriBuilder, "sort", searchParam.getSortBy());
        addParam(uriBuilder, "aggField", searchParam.getAggregationField());
        addParams(uriBuilder, "topicWildcard", searchParam.getTopicWildcards());
        addParams(uriBuilder, "topic", searchParam.getTopics());

        if (null != searchParam.getFilters()) {
            searchParam.getFilters()
                       .forEach(f -> addParam(uriBuilder, f.getKey(), f.getValue()));
        }


        if (null != searchParam.getDocTypes()) {
            for (ContentType s : searchParam.getDocTypes()) {
                addParam(uriBuilder, "type", s.name());
            }
        }

        if (ListUtil.isNotEmpty(searchParam.getQueryTypes())) {
            searchParam.getQueryTypes()
                       .forEach(s -> addParam(uriBuilder,
                                              "query",
                                              s.name()
                                               .toLowerCase()));
        }

        if (null != searchParam.getPublishDates()) {
            final PublishDates publishDates = searchParam.getPublishDates();
            addParam(uriBuilder, "releasedAfter", publishDates.publishedFrom());
            addParam(uriBuilder, "releasedBefore", publishDates.publishedTo());
        }

        final URI searchUri = uriBuilder.build();
        byte[] responseBytes = executeGet(searchUri);
        return SearchResultsFactory.getInstance(responseBytes,
                                                searchParam.getSortBy(),
                                                searchParam.getPage(),
                                                searchParam.getSize(),
                                                searchParam.getQueryTypes());

    }

    private static void addParam(final URIBuilder uriBuilder, final String key, final Boolean value) {
        if (null != value && isNotBlank(key)){
            uriBuilder.addParameter(key, value.toString());
        }
    }

    private static void addParam(final URIBuilder uriBuilder, final String key, final Date value) {
        if (null != value && isNotBlank(key)){
            uriBuilder.addParameter(key, ISO_DATE_FORMAT.format(value));

        }
    }

    private static void addParam(final URIBuilder uriBuilder, final String key, final String value) {
        if (isNotBlank(value) && isNotBlank(key)) {
            uriBuilder.addParameter(key, value);

        }
    }

    private static void addParam(final URIBuilder uriBuilder, final String key, final Enum value) {
        if (null != value && isNotBlank(key)) {
            uriBuilder.addParameter(key, value.name());

        }
    }

    private static void addParams(final URIBuilder uriBuilder, final String key, final Collection values) {

        if (null != values && values.size() > 0 && isNotBlank(key)) {
            values.forEach(v -> addParam(uriBuilder, key, Objects.toString(v)));
        }
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
     * Performs query for requested query term against filtered content types and counts contents types.
     * Content results are serialised into json with key "result" and document counts are serialised as "counts".
     * <p>
     * Accepts extra searches to perform along with content query and document counts.
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
            final Map<String, SearchResult> results = search(searchParam);


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

    public static Map<String, SearchResult> search(
            final SearchParam searchParam) throws IOException, URISyntaxException {
        final SearchResults search = query(searchParam);

        final Map<String, SearchResult> results = new HashMap<>();
        search.getResults()
              .forEach(sr -> results.put(sr.getQueryType()
                                           .getText(), sr));
        return results;
    }

    public static Map<String, SearchResult> listRelatedPages(final List<Map> uris, ContentType... docTypes) throws IOException, URISyntaxException {
        final URIBuilder builder = new URIBuilder().setScheme(SEARCH_SERVICE_SCHEME)
                .setHost("localhost")
                .setPort(10001)
                .setPath("data");

        uris.forEach(map -> builder.addParameter("uris", (String)map.get("uri")));

        Arrays.asList(docTypes).forEach(docType -> builder.addParameter("types", docType.name()));
        byte[] responseBytes = executeGet(builder.build());
        SearchResults search = SearchResultsFactory.getInstance(responseBytes, SortBy.first_letter, 10, 10, Arrays.asList(QueryType.SEARCH));
        final Map<String, SearchResult> results = new HashMap<>();
        search.getResults()
                .forEach(sr -> results.put(sr.getQueryType().getText(), sr));
        return results;
    }

    public static BabbageResponse listRelatedPage(String listType, List<Map> uris, HttpServletRequest requests, ContentType... docTypes) throws IOException, URISyntaxException {

        final Map<String, SearchResult> search = listRelatedPages(uris, docTypes);

        return buildPageResponse(listType, search);
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
     * query time series for a given uri without dealing with request / response objects.
     *
     * @param uriString
     * @return
     */
    public static Map<String, SearchResult> searchTimeseriesForUri(String uriString) throws IOException, URISyntaxException {
        SearchParam param = SearchParamFactory.getInstance()
                .addQueryTypes(Collections.singleton(QueryType.SEARCH))
                .addDocType(ContentType.timeseries)
                .setPage(1)
                .setSize(1)
                .setPrefixURI(uriString);
        Map<String, SearchResult> results = SearchUtils.search(param);
        return results;
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
