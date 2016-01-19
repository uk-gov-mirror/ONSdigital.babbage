package com.github.onsdigital.babbage.api.filter;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test the {@link UrlRedirectFilter} class. Verifies that the {@link com.github.davidcarboni.restolino.framework.Filter}
 * performs as expected in both negative and positive scenarios.
 */
public class UrlRedirectFilterTest {

	private static final String ONS_HOME_PAGE = "https://beta.ons.gov.uk";

	@Mock
	private HttpServletRequest mockRequest;

	@Mock
	private HttpServletResponse mockResponse;

	@Mock
	private UrlRedirectService urlRedirectServiceMock;

	@Mock
	private RedirectURL.Builder redirectUrlBuilderMock;

	@Mock
	private RedirectURL redirectURLMock;

	@InjectMocks
	private UrlRedirectFilter filter;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		filter = new UrlRedirectFilter();

		// Set mocks on target.
		TestsUtil.setPrivateField(filter, "urlRedirectService", urlRedirectServiceMock);
		TestsUtil.setPrivateField(filter, "redirectUrlBuilder", redirectUrlBuilderMock);
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

		verifyZeroInteractions(urlRedirectServiceMock);
	}

	/**
	 * Test verifies the filter behaves correctly for cases when the URL is a Taxonomy Redirect but is missing the required
	 * nscl parameter. FIlter should send a redirect to the ONS home page.
	 */
	@Test
	public void testFilterTaxonomyRedirectNoNscl() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html");
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.TAXONOMY_REDIRECT);
		when(redirectURLMock.containsParameter())
				.thenReturn(false);
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		filter.filter(mockRequest, mockResponse);

		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(mockResponse, times(1)).sendRedirect("/");
		verify(urlRedirectServiceMock, never()).findRedirect(any(RedirectURL.class));
		verify(urlRedirectServiceMock, never()).convertToNationalArchiveFormat(any(RedirectURL.class));
	}

	/**
	 * Test verifies that filter behaves correctly for cases where a valid taxonomy redirect is encountered.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterValidTaxonomyRequest() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html");
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.TAXONOMY_REDIRECT);
		when(redirectURLMock.containsParameter())
				.thenReturn(true);
		when(urlRedirectServiceMock.findRedirect(redirectURLMock))
				.thenReturn("successfulRedirect");
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		filter.filter(mockRequest, mockResponse);

		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(urlRedirectServiceMock, times(1)).findRedirect(redirectURLMock);
		verify(mockResponse, times(1)).sendRedirect("successfulRedirect");
		verify(urlRedirectServiceMock, never()).convertToNationalArchiveFormat(any(RedirectURL.class));
	}

	/**
	 * Test verifies that filter behaves correctly for cases where a valid taxonomy redirect is encountered but no mapping
	 * exists. In this case the request should be redirected to the national archive site.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFilterValidTaxonomyRequestNoMapping() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/taxonomy/index.html");
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.TAXONOMY_REDIRECT);
		when(redirectURLMock.containsParameter())
				.thenReturn(true);
		when(urlRedirectServiceMock.findRedirect(redirectURLMock))
				.thenReturn(null);
		when(urlRedirectServiceMock.convertToNationalArchiveFormat(redirectURLMock))
				.thenReturn("nationalArchiveUrl");
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		filter.filter(mockRequest, mockResponse);

		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(urlRedirectServiceMock, times(1)).findRedirect(redirectURLMock);
		verify(urlRedirectServiceMock, times(1)).convertToNationalArchiveFormat(redirectURLMock);
		verify(mockResponse, times(1)).sendRedirect("nationalArchiveUrl");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testFilterJPGResource() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/resources/140068097_tcm77-298212.jpg");
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.GENERAL_REDIRECT);
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(urlRedirectServiceMock.convertToNationalArchiveFormat(redirectURLMock))
				.thenReturn("nationalArchiveUrl");

		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		filter.filter(mockRequest, mockResponse);

		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(urlRedirectServiceMock, never()).findRedirect(redirectURLMock);
		verify(urlRedirectServiceMock, times(1)).convertToNationalArchiveFormat(redirectURLMock);
		verify(mockResponse, times(1)).sendRedirect("nationalArchiveUrl");
	}

	@Test
	public void testFilterPDFResource() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/rel/abs/annual-business-survey/2009-provisional-results/abs-2009---provisional-results-statistical-bulletin--nov-2010-.pdf");
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.GENERAL_REDIRECT);
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(urlRedirectServiceMock.convertToNationalArchiveFormat(redirectURLMock))
				.thenReturn("nationalArchiveUrl");

		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/taxonomy/index.html"));

		filter.filter(mockRequest, mockResponse);

		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verify(urlRedirectServiceMock, never()).findRedirect(redirectURLMock);
		verify(urlRedirectServiceMock, times(1)).convertToNationalArchiveFormat(redirectURLMock);
		verify(mockResponse, times(1)).sendRedirect("nationalArchiveUrl");
	}

	/**
	 * Verify the filter behaves correctly when a valid Data Explorer redirect is encountered.
	 */
	@Test
	public void testFilterDataExplorerRequest() throws Exception {
		when(mockRequest.getRequestURI())
				.thenReturn("/ons/data/dataset-finder");
		when(redirectURLMock.getCategory())
				.thenReturn(RedirectCategory.DATA_EXPLORER_REDIRECT);
		when(redirectUrlBuilderMock.build(mockRequest))
				.thenReturn(redirectURLMock);
		when(redirectURLMock.getUrl())
				.thenReturn(new URL("http://localhost:8080/ons/data/dataset-finder"));

		filter.filter(mockRequest, mockResponse);

		verify(urlRedirectServiceMock, times(1)).convertToDataExplorerFormat(redirectURLMock);
		verify(redirectUrlBuilderMock, times(1)).build(mockRequest);
		verifyNoMoreInteractions(urlRedirectServiceMock);
	}

}
