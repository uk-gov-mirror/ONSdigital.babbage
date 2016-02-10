package com.github.onsdigital.babbage.api.endpoint.rss.service;

import com.github.onsdigital.babbage.api.endpoint.rss.RssSearchFilter;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.TestsUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test verifies that {@link RssService} behaves as expected in negative and positive scenarios.
 */
public class RssServiceTest {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(Configuration.CONTENT_SERVICE.getDefaultContentDatePattern());
	private static final String HOST = "CountDracula/";
	private static final String TITLE = "Title";
	private static final String META_DESC = "Meta-Description";
	private static final String RELEASE_DATE = "2015-11-27T00:00:00.000Z";
	private static final String URI = "you/are/eye";

	@Mock
	private RssSearchFilter filterMock;

	@Mock
	private RequestUtil.Location locationMock;

	@Mock
	private PropertiesService propertiesService;

	@Mock
	private RssSearchService searchService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private SearchQueries searchQueries;

	private RssService service;
	private String defaultTitle;
	private String rssType;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		service = RssService.getInstance();

		TestsUtil.setPrivateField(service, "propertiesService", propertiesService);
		TestsUtil.setPrivateStaticField(service, "rssSearchService", searchService);

		ThreadContext.addData(RequestUtil.LOCATION_KEY, locationMock);

		when(locationMock.getHost())
				.thenReturn(HOST);

		defaultTitle = PropertiesService.getInstance().get("rss.title.default");
		rssType = PropertiesService.getInstance().get("rss.type");
	}

	/**
	 * Test Case: Search does not return any results.
	 */
	@Test
	public void testGetFeedNoResults() throws Exception {
		when(searchService.generateQuery(filterMock))
				.thenReturn(searchQueries);
		when(searchService.search(searchQueries))
				.thenReturn(Optional.empty());

		SyndFeed result = service.getFeed(filterMock);

		assertThat("Incorrect Feed Title.", result.getTitle(), equalTo(defaultTitle));
		assertThat("Incorrect RSS type.", result.getFeedType(), equalTo(rssType));
		assertThat("Incorrect Link.", result.getLink(), equalTo(HOST));
		assertTrue("Feed.entries should be empty.", result.getEntries().isEmpty());

		verify(searchService, times(1)).generateQuery(filterMock);
		verify(searchService, times(1)).search(searchQueries);
		verifyZeroInteractions(propertiesService);
	}

	/**
	 * Test Case: Service returns a Feed containing entries.
	 */
	@Test
	public void testGetFeedResults() throws Exception {
		when(searchService.generateQuery(filterMock))
				.thenReturn(searchQueries);
		when(searchService.search(searchQueries))
				.thenReturn(Optional.of(searchResults()));

		SyndFeed feed = service.getFeed(filterMock);

		assertThat("Incorrect Feed Title.", feed.getTitle(), equalTo(defaultTitle));
		assertThat("Incorrect RSS type.", feed.getFeedType(), equalTo(rssType));
		assertThat("Incorrect Link.", feed.getLink(), equalTo(HOST));
		assertTrue("Feed.entries should be empty.", feed.getEntries().size() == 1);

		SyndEntry entry = (SyndEntry) feed.getEntries().get(0);

		assertThat("Incorrect Title.", entry.getTitle(), equalTo(TITLE));
		assertThat("Incorrect Meta Description.", entry.getDescription().getValue(), equalTo(META_DESC));

		assertThat("Incorrect Release Date.", entry.getPublishedDate(), equalTo(DATE_FORMAT.parse(RELEASE_DATE)));
		assertThat("Incorrect URI.", entry.getLink(), equalTo("http://" + HOST + URI));

		verify(searchService, times(1)).generateQuery(filterMock);
		verify(searchService, times(1)).search(searchQueries);
		verifyZeroInteractions(propertiesService);
	}

	private SearchResult searchResults() {
		List<Map<String, Object>> resultsListMap = new ArrayList<>();

		Map<String, Object> descMap = new HashMap<>();
		descMap.put("title", TITLE);
		descMap.put("metaDescription", META_DESC);
		descMap.put("releaseDate", RELEASE_DATE);

		Map<String, Object> masterMap = new HashMap<>();
		masterMap.put("description", descMap);
		masterMap.put("uri", URI);

		resultsListMap.add(masterMap);
		SearchResult searchResult = new SearchResult();
		searchResult.setResults(Arrays.asList(masterMap));
		return searchResult;
	}

}
