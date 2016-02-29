package com.github.onsdigital.babbage.api.filter;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;
import com.github.onsdigital.babbage.util.TestsUtil;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test the {@link UrlRedirectFilter} class. Verifies that the {@link com.github.davidcarboni.restolino.framework.Filter}
 * performs as expected in both negative and positive scenarios.
 */
public class UrlRedirectFilterTest {

	@Mock
	private HttpServletRequest mockRequest;

	@Mock
	private HttpServletResponse mockResponse;

	@Mock
	private RedirectHandler taxonomyHandlerMock, generalHandlerMock, dataExHandlerMock;

	@InjectMocks
	private UrlRedirectFilter filter;

	private ImmutableMap<RedirectCategory, RedirectHandler> handlers;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		filter = new UrlRedirectFilter();

		handlers = new ImmutableMap.Builder<RedirectCategory, RedirectHandler>()
				.put(TAXONOMY_REDIRECT, taxonomyHandlerMock)
				.put(DATA_EXPLORER_REDIRECT, dataExHandlerMock)
				.put(GENERAL_REDIRECT, generalHandlerMock)
				.build();

		// Set mocks on target.
		TestsUtil.setPrivateField(filter, "handlers", handlers);
	}

	@Test
	public void shouldNotRedirect() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/no/redirect/required.html");

		assertThat("Incorrect filter result test failed.", filter.filter(mockRequest, mockResponse), equalTo(true));
		verifyZeroInteractions(taxonomyHandlerMock, dataExHandlerMock, generalHandlerMock);
	}

	@Test
	public void shouldCallTaxonomyRedirectHandler() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html");

		assertThat("Incorrect filter result test failed.", filter.filter(mockRequest, mockResponse),
				equalTo(false));

		verify(taxonomyHandlerMock, times(1)).handle(mockRequest, mockResponse);
		verifyZeroInteractions(generalHandlerMock, dataExHandlerMock);
	}

	@Test
	public void shouldCallGeneralRedirectHandler() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/resources/140068097_tcm77-298212.jpg");

		assertThat("Incorrect filter result test failed.", filter.filter(mockRequest, mockResponse), equalTo(false));
		verify(generalHandlerMock, times(1)).handle(mockRequest, mockResponse);
		verifyZeroInteractions(taxonomyHandlerMock, dataExHandlerMock);
	}

	@Test
	public void shouldCallDataExplorerRedirectHandler() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/data/dataset-finder");

		assertThat("Incorrect filter result test failed.", filter.filter(mockRequest, mockResponse), equalTo(false));
		verify(dataExHandlerMock, times(1)).handle(mockRequest, mockResponse);
		verifyZeroInteractions(taxonomyHandlerMock, generalHandlerMock);
	}

	@Test
	public void shouldInvokeErrorHandler() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html")
				.thenReturn("/somethingRandom");

		filter.filter(mockRequest, mockResponse);

		verify(mockResponse, times(1)).setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		verifyZeroInteractions(taxonomyHandlerMock, generalHandlerMock, dataExHandlerMock);
	}

}
