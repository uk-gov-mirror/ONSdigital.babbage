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
	private static final RssService INSTANCE = new RssService();
	private static final RssSearchService rssSearchService = RssSearchService.getInstance();

	private final PropertiesService propertiesService;
	private String rssType;

	public static RssService getInstance() {
		return RssService.INSTANCE;
	}

	private RssService() {
		this.propertiesService = PropertiesService.getInstance();
		this.rssType = propertiesService.get(RSS_TYPE_KEY);
	}

	public SyndFeed getFeed(RssSearchFilter filter) {
		SearchQueries queries = rssSearchService.generateQuery(filter);
		return generateSyndFeed(rssSearchService.search(queries), filter);
	}

	private SyndFeed generateSyndFeed(Optional<SearchResult> results, RssSearchFilter filter) {
		return new SyndFeedBuilder()
				.type(rssType)
				.link(((Location) ThreadContext.getData(LOCATION_KEY)).getHost())
				.category(filter.getUri())
				.entries(searchResultsToSyndEntries(results))
				.title(filter)
				.build();
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
