package com.github.onsdigital.babbage.api.filter;

import com.github.onsdigital.babbage.url.shortcuts.ShortcutUrlService;
import com.github.onsdigital.babbage.util.TestsUtil;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test the shortcut filter works as expected in each scenario.
 */
public class ShortUrlFilterTest {

	private static final String SHORT_URL = "/shortcut";
	private static final String SHORT_URL_REDIRECT = "/shortcut/redirected/to/here";

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private ShortcutUrlService shortcutUrlService;

	private ShortUrlFilter filter;

	private Map<String, String> shortcutMap = new ImmutableMap.Builder<String, String>()
			.put(SHORT_URL, SHORT_URL_REDIRECT)
			.build();

	private Optional<Map<String, String>> mapOptional = Optional.of(shortcutMap);


	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		filter = new ShortUrlFilter();

		TestsUtil.setPrivateField(filter, "shortcutUrlService", shortcutUrlService);
	}

	@Test
	public void shouldLoadShortcutMappingAndNotRedirect() throws Exception {
		when(request.getRequestURI())
				.thenReturn("/notAShortcut");
		when(shortcutUrlService.shortcuts())
				.thenReturn(shortcutMap);

		TestsUtil.setPrivateStaticField(filter, "shortcuts", Optional.empty());

		boolean result = filter.filter(request, response);

		assertThat("Expected filter to return true but was false. Test failed.", result, equalTo(true));
		verify(shortcutUrlService, times(1)).shortcuts();
		verifyZeroInteractions(response);
	}

	@Test
	public void shouldNotLoadShortcutMappingAndNotRedirect() throws Exception {
		when(request.getRequestURI())
				.thenReturn("/notAShortcut");

		TestsUtil.setPrivateStaticField(filter, "shortcuts", mapOptional);

		boolean result = filter.filter(request, response);

		assertThat("Expected filter to return true but was false. Test failed.", result, equalTo(true));
		verifyZeroInteractions(shortcutUrlService, response);
	}

	@Test
	public void shouldLoadShortcutMappingAndRedirect() throws Exception {
		when(request.getRequestURI())
				.thenReturn(SHORT_URL);
		when(shortcutUrlService.shortcuts())
				.thenReturn(shortcutMap);

		TestsUtil.setPrivateStaticField(filter, "shortcuts", Optional.empty());

		boolean result = filter.filter(request, response);

		assertThat("Expected filter to return false but was true. Test failed.", result, equalTo(false));
		verify(shortcutUrlService, times(1)).shortcuts();
		verify(response, times(1)).sendRedirect(SHORT_URL_REDIRECT);
		verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	@Test
	public void shouldNotLoadShortcutMappingAndShouldRedirect() throws Exception {
		when(request.getRequestURI())
				.thenReturn(SHORT_URL);

		TestsUtil.setPrivateStaticField(filter, "shortcuts", mapOptional);

		boolean result = filter.filter(request, response);

		assertThat("Expected filter to return false but was true. Test failed.", result, equalTo(false));
		verifyZeroInteractions(shortcutUrlService);
		verify(response, times(1)).sendRedirect(SHORT_URL_REDIRECT);
		verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionWhenSettingRedirect() throws Exception {
		when(request.getRequestURI())
				.thenReturn(SHORT_URL);

		doThrow(new IOException())
				.when(response).sendRedirect(anyString());

		TestsUtil.setPrivateStaticField(filter, "shortcuts", mapOptional);

		try {
			filter.filter(request, response);
		} catch (Exception ex) {
			assertTrue("Expected IOException.", ex.getCause() instanceof IOException);
			throw new IOException(ex.getCause());
		}
	}
}
