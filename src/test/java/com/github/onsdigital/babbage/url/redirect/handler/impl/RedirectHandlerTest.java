package com.github.onsdigital.babbage.url.redirect.handler.impl;

import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests each implementation of {@link RedirectHandler}.
 */
public class RedirectHandlerTest {

	private static final String REDIRECT_MAPPING = "/redirected";
	private static final String TNA_REDIRECT = "TNA";
	private static final String REDIRECT_SERVICE_NAME = "urlRedirectService";

	@Mock
	private RedirectURL redirectURL;

	@Mock
	private HttpServletResponse response;

	@Mock
	private UrlRedirectService urlRedirectService;

	private RedirectHandler dataXHandler, taxonomyHandler, generalHandler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dataXHandler = new DataExplorerRedirectHandler();
		TestsUtil.setPrivateField(dataXHandler, REDIRECT_SERVICE_NAME, urlRedirectService);

		taxonomyHandler = new TaxonomyRedirectHandler();
		TestsUtil.setPrivateField(taxonomyHandler, REDIRECT_SERVICE_NAME, urlRedirectService);

		generalHandler = new GeneralRedirectHandler();
		TestsUtil.setPrivateField(generalHandler, REDIRECT_SERVICE_NAME, urlRedirectService);
	}

	/**
	 * Test verifies the {@link DataExplorerRedirectHandler} behaves correctly.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testDataExplorerHandler() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(DATA_EXPLORER_REDIRECT);
		when(urlRedirectService.convertToDataExplorerFormat(redirectURL))
				.thenReturn(REDIRECT_MAPPING);

		dataXHandler.handle(redirectURL, response);

		verify(urlRedirectService, times(1)).convertToDataExplorerFormat(redirectURL);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly for valid taxonomy redirects.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testValidTaxonomyHandler() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURL.containsParameter(TAXONOMY_REDIRECT.getParameterName()))
				.thenReturn(true);
		when(urlRedirectService.findRedirect(redirectURL))
				.thenReturn(REDIRECT_MAPPING);

		taxonomyHandler.handle(redirectURL, response);

		verify(urlRedirectService, times(1)).findRedirect(redirectURL);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly for taxonomy redirects that do not contain the
	 * nscl parameter.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testTaxonomyHandlerMissingParam() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURL.containsParameter(TAXONOMY_REDIRECT.getParameterName()))
				.thenReturn(false);

		taxonomyHandler.handle(redirectURL, response);

		verifyZeroInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect("/");
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly for valid taxonomy redirects where no mapping is
	 * found.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testTaxonomyHandlerNoMappingFound() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURL.containsParameter(TAXONOMY_REDIRECT.getParameterName()))
				.thenReturn(true);
		when(urlRedirectService.findRedirect(redirectURL))
				.thenReturn(null);
		when(urlRedirectService.convertToNationalArchiveFormat(redirectURL))
				.thenReturn(TNA_REDIRECT);

		taxonomyHandler.handle(redirectURL, response);

		verify(urlRedirectService, times(1)).findRedirect(redirectURL);
		verify(urlRedirectService, times(1)).convertToNationalArchiveFormat(redirectURL);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(TNA_REDIRECT);
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly cases where the {@link RedirectURL} param is null.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = NullPointerException.class)
	public void testTaxonomyHandlerWithNullRedirectUrl() throws Exception {
		try {
			taxonomyHandler.handle(null, response);
		} catch (Exception ex) {
			verifyZeroInteractions(urlRedirectService, response);
			throw ex;
		}
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly cases where the {@link RedirectURL} param is null.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = NullPointerException.class)
	public void testTaxonomyHandlerWithNullResponse() throws Exception {
		try {
			taxonomyHandler.handle(redirectURL, null);
		} catch (Exception ex) {
			verifyZeroInteractions(urlRedirectService, response);
			throw ex;
		}
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly cases where the {@link RedirectURL} param is null.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = RedirectException.class)
	public void testTaxonomyHandlerWithInvalidCategory() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		try {
			taxonomyHandler.handle(redirectURL, response);
		} catch (RedirectException ex) {
			verifyZeroInteractions(urlRedirectService, response);
			assertThat("Incorrect ErrorType, test failed.", RedirectException.ErrorType.INVALID_REDIRECT_CATEGORY,
					equalTo(ex.getErrorType()));
			throw ex;
		}
	}

	/**
	 * Test verifies {@link GeneralRedirectHandler} behaves correctly for valid taxonomy redirects where no mapping is
	 * found.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testGeneralHandlerNoMappingFound() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		when(urlRedirectService.findRedirect(redirectURL))
				.thenReturn(null);
		when(urlRedirectService.convertToNationalArchiveFormat(redirectURL))
				.thenReturn(TNA_REDIRECT);

		generalHandler.handle(redirectURL, response);

		verify(urlRedirectService, times(1)).findRedirect(redirectURL);
		verify(urlRedirectService, times(1)).convertToNationalArchiveFormat(redirectURL);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(TNA_REDIRECT);
	}

	/**
	 * Test verifies {@link TaxonomyRedirectHandler} behaves correctly for valid taxonomy redirects.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testGeneralHandler() throws Exception {
		when(redirectURL.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		when(urlRedirectService.findRedirect(redirectURL))
				.thenReturn(REDIRECT_MAPPING);

		generalHandler.handle(redirectURL, response);

		verify(urlRedirectService, times(1)).findRedirect(redirectURL);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}
}
