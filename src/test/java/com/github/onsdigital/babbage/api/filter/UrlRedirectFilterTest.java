package com.github.onsdigital.babbage.api.filter;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
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
import java.net.URL;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
	private RedirectURL.Builder redirectUrlBuilderMock;

	@Mock
	private RedirectURL redirectURLMock;

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
		TestsUtil.setPrivateField(filter, "redirectUrlBuilder", redirectUrlBuilderMock);
		TestsUtil.setPrivateField(filter, "handlers", handlers);
	}

	/**
	 * Test verifies filter behaves correctly for cases where no redirect is required.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterNoRedirectRequired() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/no/redirect/required.html");

		filter.filter(mockRequest, mockResponse);

		assertThat("Incorrect filter result test failed.", filter.filter(mockRequest, mockResponse), is(true));
		verifyZeroInteractions(taxonomyHandlerMock, dataExHandlerMock, generalHandlerMock, redirectUrlBuilderMock);
	}

	/**
	 * Test verifies filter behaves correctly for cases where a Taxonomy Redirect URL is encountered. Test verifies the
	 * correct {@link RedirectHandler} is called.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterTaxonomyRequest() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html");
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURLMock.containsParameter(TAXONOMY_REDIRECT.getParameterName()))
				.thenReturn(false);
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		boolean result = filter.filter(mockRequest, mockResponse);

		assertThat("Incorrect filter result test failed.", result, is(false));
		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(taxonomyHandlerMock, times(1)).handle(redirectURLMock, mockResponse);
		verifyZeroInteractions(generalHandlerMock, dataExHandlerMock);
	}

	/**
	 * Test verifies filter behaves correctly for cases where a General Redirect URL is encountered. Test verifies the
	 * correct {@link RedirectHandler} is called.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterGeneralRedirect() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/resources/140068097_tcm77-298212.jpg");
		when(redirectURLMock.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		boolean result = filter.filter(mockRequest, mockResponse);

		assertThat("Incorrect filter result test failed.", result, is(false));
		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(generalHandlerMock, times(1)).handle(redirectURLMock, mockResponse);
		verifyZeroInteractions(taxonomyHandlerMock, dataExHandlerMock);
	}

	/**
	 * Test verifies filter behaves correctly for cases where a Data Explorer Redirect URL is encountered. Test verifies
	 * the correct {@link RedirectHandler} is called.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterDataExplorerRequest() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/data/dataset-finder");
		when(redirectURLMock.getCategory())
				.thenReturn(DATA_EXPLORER_REDIRECT);
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/data/dataset-finder"));

		boolean result = filter.filter(mockRequest, mockResponse);

		assertThat("Incorrect filter result test failed.", result, is(false));
		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(dataExHandlerMock, times(1)).handle(redirectURLMock, mockResponse);
		verifyZeroInteractions(taxonomyHandlerMock, generalHandlerMock);
	}

}
