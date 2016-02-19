package com.github.onsdigital.babbage.api.endpoint.rss.service;

import com.github.onsdigital.babbage.api.endpoint.rss.RssSearchFilter;
import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndEntryBuilder;
import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndFeedBuilder;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.onsdigital.babbage.util.RequestUtil.LOCATION_KEY;
import static com.github.onsdigital.babbage.util.RequestUtil.Location;

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
	private static final RssSearchService rssSearchService = RssSearchService.getInstance();

	private final PropertiesService propertiesService;
	private String rssType;
	private String calendarUpcomingTitle;
	private String calendarPublishedTitle;
	private String calendarTitleTwo;

	public static RssService getInstance() {
		return RssService.INSTANCE;
	}

	private RssService() {
		this.propertiesService = PropertiesService.getInstance();
		this.rssType = propertiesService.get(RSS_TYPE_KEY);
		this.calendarUpcomingTitle = propertiesService.get(RSS_CALENDAR_UPCOMING_TITLE_KEY);
		this.calendarPublishedTitle = propertiesService.get(RSS_CALENDAR_PUBLISHED_TITLE_KEY);
		this.calendarTitleTwo = propertiesService.get(RSS_CALENDAR_TITLE_TWO);
	}

	public SyndFeed getFeed(RssSearchFilter filter) {
		SearchQueries queries = rssSearchService.generateQuery(filter);
		return generateSyndFeed(rssSearchService.search(queries), filter);
	}

	public SyndFeed generateSyndFeed(Optional<SearchResult> results, RssSearchFilter filter) {
		return new SyndFeedBuilder()
				.type(rssType)
				.link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
				.category(filter.getUri())
				.entries(searchResultsToSyndEntries(results))
				.title(filter)
				.build();
	}

	public SyndFeed generateCalendarSyndFeed(Optional<SearchResult> results, HttpServletRequest request) {
		StringBuilder title = new StringBuilder(calendarTitle(request));

		String query;
		if ((query = request.getParameter("query")) != null) {
			title.append(" ").append(String.format(calendarTitleTwo, query));
		}

		return new SyndFeedBuilder()
				.type(rssType)
				.link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
				.category(title.toString())
				.entries(searchResultsToSyndEntries(results))
				.title(title.toString())
				.build();
	}

	private String calendarTitle(HttpServletRequest request) {
		return "upcoming".equalsIgnoreCase(request.getParameter("view")) ? calendarUpcomingTitle : calendarPublishedTitle;
	}

	/**
	 * Convert the Search Response to a {@link List} of {@link SyndEntry}.
	 */
	private List<SyndEntry> searchResultsToSyndEntries(Optional<SearchResult> results) {
		List<SyndEntry> feed = new ArrayList<>();

		if (results.isPresent()) {
			feed = results.get().getResults()
					.stream()
					.map(nestedMap -> new SyndEntryBuilder().build(nestedMap))
					.collect(Collectors.toList());
		}
		return feed;
	}
}
