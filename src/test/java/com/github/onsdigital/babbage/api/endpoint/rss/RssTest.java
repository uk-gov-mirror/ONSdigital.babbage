package com.github.onsdigital.babbage.api.endpoint.rss;

import com.github.onsdigital.babbage.api.endpoint.rss.builder.SyndFeedBuilder;
import com.github.onsdigital.babbage.api.endpoint.rss.service.RssService;
import com.github.onsdigital.babbage.util.TestsUtil;
import com.sun.syndication.feed.synd.SyndFeed;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test verifies that {@link Rss} endpoint behaves as expected.
 */
public class RssTest {

	private static final String URI = "/businessindustryandtrade/itandinternetindustry/publications";
	private static final String FILTER_BULLENTIN = "bulletin";
	private static final String FILTER_ARTICLE = "article";
	private static final String FILTER_QUERY = "public";

	private Rss rss;
	private Map<String, String[]> requestParameters;
	private SyndFeed syndFeed;

	@Mock
	private RssService rssServiceMock;

	@Mock
	private HttpServletRequest requestMock;

	@Mock
	private HttpServletResponse responseMock;

	@Mock
	private RssSearchFilter.Builder filterBuilder;

	@Mock
	private RssSearchFilter rssSearchFilter;

	@Mock
	private PrintWriter mockWriter;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		rss = new Rss();
		syndFeed = new SyndFeedBuilder()
				.type("rss_2.0")
				.title("test")
				.desc("test")
				.link("link")
				.build();

		TestsUtil.setPrivateField(rss, "filterBuilder", filterBuilder);

		requestParameters = new HashMap<>();
		requestParameters.put("uri", new String[]{URI});
		requestParameters.put("filter", new String[]{FILTER_BULLENTIN, FILTER_ARTICLE});
		requestParameters.put("query", new String[]{FILTER_QUERY});

		TestsUtil.setPrivateField(rss, "rssService", rssServiceMock);
	}

	/**
	 * Test the endpoint.
	 */
	@Test
	public void testRSS() throws Exception {
		when(filterBuilder.build(requestMock))
				.thenReturn(rssSearchFilter);
		when(rssServiceMock.getFeed(rssSearchFilter))
				.thenReturn(syndFeed);
		when(responseMock.getWriter())
				.thenReturn(mockWriter);

		rss.doGet(requestMock, responseMock);

		verify(filterBuilder, times(1)).build(requestMock);
		verify(rssServiceMock, times(1)).getFeed(rssSearchFilter);
		verify(responseMock, times(1)).getWriter();
	}
}
