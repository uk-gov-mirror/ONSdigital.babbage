package com.github.onsdigital.babbage.api.endpoint.rss.service;

import com.github.onsdigital.babbage.api.endpoint.rss.RssSearchFilter;
import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndEntryBuilder;
import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndFeedBuilder;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.response.BabbageRssResponse;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
import static com.github.onsdigital.babbage.search.model.field.Field.releaseDate;
import static com.github.onsdigital.babbage.util.RequestUtil.LOCATION_KEY;
import static com.github.onsdigital.babbage.util.RequestUtil.Location;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * Service provides functionality required for the ONS RSS feed.
 * <ul>
 * <li>Generates the {@link ONSQuery} to search for feed entries by {@link ContentType}.</li>
 * <li>Converts search results the required {@link SyndEntry} format.</li>
 * <li>Generates a {@link SyndFeed} from a {@link List} of {@link SyndEntry}</li>
 * </ul>
 */
public class RssService {

    private static final String RSS_TYPE_KEY = "rss.type";
    private static final String RSS_CALENDAR_PUBLISHED_TITLE_KEY = "rss.calendar.published.title";
    private static final String RSS_CALENDAR_UPCOMING_TITLE_KEY = "rss.calendar.upcoming.title";
    private static final String RSS_CALENDAR_TITLE_TWO = "rss.calendar.title.two";
    private static final RssService INSTANCE = new RssService();

    private static final String RSS_MAX_FEED_SIZE_KEY = "rss.max.results.size";
    private static final String FEED_DATE_RANGE_KEY = "rss.feed.data.range";
    private static final String RESULTS_KEY = "result";
    public static final String RSS_TITLE_DEFAULT = "rss.title.default";
    private final PropertiesService propertiesService;
    private final String defaultTitle;
    private String rssType;
    private String calendarUpcomingTitle;
    private String calendarPublishedTitle;
    private String calendarTitleTwo;

    private RssService() {
        this.propertiesService = PropertiesService.getInstance();
        this.rssType = propertiesService.get(RSS_TYPE_KEY);
        this.calendarUpcomingTitle = propertiesService.get(RSS_CALENDAR_UPCOMING_TITLE_KEY);
        this.calendarPublishedTitle = propertiesService.get(RSS_CALENDAR_PUBLISHED_TITLE_KEY);
        this.calendarTitleTwo = propertiesService.get(RSS_CALENDAR_TITLE_TWO);
        this.defaultTitle = propertiesService.get(RSS_TITLE_DEFAULT);
    }

    public static RssService getInstance() {
        return RssService.INSTANCE;
    }

    /**
     * @return true is the request is an rss request, false otherwise.
     */
    public boolean isRssRequest(HttpServletRequest request) {
        return request.getParameterMap()
                      .entrySet()
                      .stream()
                      .filter(entry -> entry.getKey()
                                            .equalsIgnoreCase(SearchParamFactory.RSS_PARAM))
                      .findFirst()
                      .isPresent();
    }

    /**
     * @return a {@link SyndFeed} for the Publication RSS feed defined by the requested parameters.
     */
    public SyndFeed getPublicationListFeed(HttpServletRequest request) {
        return listFeed(request);
    }

    /**
     * @return a {@link BabbageRssResponse} for the Publications RSS feed defined by the requested parameters.
     */
    public BabbageRssResponse getPublicationListFeedResponse(HttpServletRequest request) {
        return writeToResponse(listFeed(request));
    }

    /**
     * @return a {@link BabbageRssResponse} for the Data RSS feed defined by the requested parameters.
     */
    public BabbageRssResponse getDataListFeedResponse(SearchParam request,
                                                      String uri) throws IOException, URISyntaxException {
        return writeToResponse(listFeed(request, uri));
    }


    /**
     * @return a {@link SyndFeed} for the Release Calendar RSS feed defined by the requested parameters.
     */
    public SyndFeed getReleaseCalendarFeed(HttpServletRequest request, SearchQueries searchQueries) {
        Optional<SearchResult> results = search(searchQueries);
        StringBuilder title = new StringBuilder(calendarTitle(request));
        String query;

        if ((query = request.getParameter("query")) != null) {
            title.append(" ")
                 .append(format(calendarTitleTwo, query));
        }

        return new SyndFeedBuilder()
                .type(rssType)
                .link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
                .category(title.toString())
                .entries(searchResultsToSyndEntries(results))
                .title(title.toString())
                .build();
    }

    /**
     * @return a {@link BabbageRssResponse} for the Release Calendar RSS feed defined by the requested parameters.
     */
    public BabbageRssResponse getReleaseCalendarFeedResponse(HttpServletRequest request, SearchQueries searchQueries) {
        return writeToResponse(getReleaseCalendarFeed(request, searchQueries));
    }

    /**
     * Write the {@Link SyndFeed} to a new {@link BabbageRssResponse} object.
     */
    public BabbageRssResponse writeToResponse(SyndFeed feed) {
        return new BabbageRssResponse.Builder().build(feed);
    }

    //TODO REMOVE
    private SyndFeed listFeed(HttpServletRequest request) {
        RssSearchFilter searchFilter = new RssSearchFilter.Builder().build(request);
        SearchQueries searchQueries = generateListQuery(searchFilter);
        Optional<SearchResult> searchResult = search(searchQueries);
        return generateSyndFeed(searchResult, searchFilter);
    }

    private SyndFeed listFeed(SearchParam params, String uri) throws IOException, URISyntaxException {

        params.addTopicWildcard(uri + "*")
              .setPrefixURI(uri);
        params.setSize(getRssSize());
        params.setPublishDates(getFeedRange());
        params.setSortBy(SortBy.release_date);
        params.setHighlights(false);

        final Map<String, SearchResult> search = SearchUtils.search(params);
        Optional<SearchResult> searchResult = Optional.ofNullable(search.get(SEARCH.getText()));
        return generateSyndFeed(searchResult, params, uri);
    }

    public Integer getRssSize() {
        return Integer.valueOf(PropertiesService.getInstance()
                                                .get(RSS_MAX_FEED_SIZE_KEY));
    }

    public PublishDates getFeedRange() {
        final String age = PropertiesService.getInstance()
                                            .get(FEED_DATE_RANGE_KEY);
        return PublishDates.updatedWithinPeriod(age);

    }

    private SearchQueries generateListQuery(RssSearchFilter filter) {
        SearchFilter searchFilter = (listQuery) -> {
            if (!StringUtils.isEmpty(filter.getUri())) {

                String uri = filter.getUri();
                listQuery.filter(
                        boolQuery().should(QueryBuilders.wildcardQuery(Field.topics.fieldName(), uri + "*"))
                                   .should(prefixQuery(Field.uri.fieldName(), endsWith(uri, "/") ? uri : uri + "/")));
            }
            listQuery.filter(QueryBuilders.rangeQuery(releaseDate.fieldName())
                                          .gte(
                                                  PropertiesService.getInstance()
                                                                   .get(FEED_DATE_RANGE_KEY))
                            );
        };

        ONSQuery onsQuery = SearchUtils.buildListQuery(filter.getRequest(), filter.getTypeFilters(), searchFilter)
                                       .size(Integer.valueOf(PropertiesService.getInstance()
                                                                              .get(RSS_MAX_FEED_SIZE_KEY)))
                                       .sortBy(SortBy.release_date)
                                       .highlight(false);

        return () -> toList(onsQuery);
    }

    private Optional<SearchResult> search(SearchQueries searchQueries) {
        LinkedHashMap<String, SearchResult> results = SearchUtils.searchAll(searchQueries);
        return Optional.ofNullable(results.get(RESULTS_KEY));
    }


    private SyndFeed generateSyndFeed(Optional<SearchResult> results, SearchParam params, String uri) {
        return new SyndFeedBuilder()
                .type(rssType)
                .link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
                .category(uri)
                .entries(searchResultsToSyndEntries(results))
                .title(toTitle(params, uri))
                .build();
    }

    private String toTitle(final SearchParam params, String uri) {
        StringBuilder title = new StringBuilder(defaultTitle);

        List<String> titleElmts = new ArrayList<>();
        titleElmts.add(format("rss.uri", uri));
        titleElmts.add(format("rss.resultstype", params.getRequestType()));
        titleElmts.add(format("rss.filters", join(params.getDocTypes(), ",")));
        titleElmts.add(format("rss.keyword", params.getSearchTerm()));


        final String specifics = titleElmts.stream().filter(s -> isNotBlank(s)).collect(Collectors.joining(", "));
        final String specific = String.format(propertiesService.get("rss.title.specific"), specifics);
        title.append(" ")
             .append(specific);
        return title.toString();
    }

    private String format(final String key, final String detail) {
        return (isNotBlank(detail) ? String.format(propertiesService.get(key), detail) : null);
    }

    //TODO REMOVE
    private SyndFeed generateSyndFeed(Optional<SearchResult> results, RssSearchFilter filter) {
        return new SyndFeedBuilder()
                .type(rssType)
                .link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
                .category(filter.getUri())
                .entries(searchResultsToSyndEntries(results))
                .title(filter)
                .build();
    }

    private String calendarTitle(HttpServletRequest request) {
        return "upcoming".equalsIgnoreCase(request.getParameter("view")) ? calendarUpcomingTitle : calendarPublishedTitle;
    }

    /**
     * Convert the Search QueryType to a {@link List} of {@link SyndEntry}.
     */
    private List<SyndEntry> searchResultsToSyndEntries(Optional<SearchResult> results) {
        List<SyndEntry> feed = new ArrayList<>();

        if (results.isPresent()) {
            feed = results.get()
                          .getResults()
                          .stream()
                          .map(nestedMap -> new SyndEntryBuilder().build(nestedMap))
                          .collect(Collectors.toList());
        }
        return feed;
    }
}
