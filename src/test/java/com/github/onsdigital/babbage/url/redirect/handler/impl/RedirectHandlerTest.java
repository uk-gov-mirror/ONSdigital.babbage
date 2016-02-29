package com.github.onsdigital.babbage.url.redirect.handler.impl;

import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
	private static final String REDIRECT_SERVICE_NAME = "urlRedirectService";

	@Mock
	private HttpServletResponse response;

	@Mock
	private HttpServletRequest request;

	@Mock
	private UrlRedirectService urlRedirectService;

	private RedirectHandler dataXHandler, taxonomyHandler, generalHandler;

	private Map<String, String[]> params;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dataXHandler = new DataExplorerRedirectHandler();
		TestsUtil.setPrivateField(dataXHandler, REDIRECT_SERVICE_NAME, urlRedirectService);

		taxonomyHandler = new TaxonomyRedirectHandler();
		TestsUtil.setPrivateField(taxonomyHandler, REDIRECT_SERVICE_NAME, urlRedirectService);

		generalHandler = new GeneralRedirectHandler();
		TestsUtil.setPrivateField(generalHandler, REDIRECT_SERVICE_NAME, urlRedirectService);

		params = new HashMap<>();
	}

	@Test
	public void shouldSuccessfullyHandleDataExplorerRedirect() throws Exception {
		when(urlRedirectService.dataExplorerRedirect(request))
				.thenReturn(REDIRECT_MAPPING);

		dataXHandler.handle(request, response);

		verify(urlRedirectService, times(1)).dataExplorerRedirect(request);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}

	@Test
	public void shouldSuccessfullyHandleTaxonomyRedirect() throws Exception {
		String nsclValue = "One+Two+Three";
		params.put("nscl", new String[]{nsclValue});

		String encodedNSCL = URLEncoder.encode(nsclValue.toLowerCase(), "UTF-8");

		when(request.getParameterMap())
				.thenReturn(params);
		when(urlRedirectService.taxonomyRedirect(encodedNSCL))
				.thenReturn(REDIRECT_MAPPING);

		taxonomyHandler.handle(request, response);

		verify(urlRedirectService, times(1)).taxonomyRedirect(encodedNSCL);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}

	@Test
	public void shouldRedirectTaxonomyRequestToHomePageIfNSCLParamIsMissing() throws Exception {
		when(request.getParameterMap())
				.thenReturn(params);

		taxonomyHandler.handle(request, response);

		verifyZeroInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect("/");
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenRequestParamIsNull() throws Exception {
		try {
			taxonomyHandler.handle(null, response);
		} catch (Exception ex) {
			verifyZeroInteractions(urlRedirectService, response);
			throw ex;
		}
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenResponseParamIsNull() throws Exception {
		try {
			taxonomyHandler.handle(request, null);
		} catch (Exception ex) {
			verifyZeroInteractions(urlRedirectService, response);
			throw ex;
		}
	}

	@Test
	public void shouldHandleGeneralRedirect() throws Exception {
		String requestedURI = "/you/Are/Eye";
		when(request.getRequestURI())
				.thenReturn(requestedURI);

		when(urlRedirectService.generalRedirect(requestedURI))
				.thenReturn(REDIRECT_MAPPING);

		generalHandler.handle(request, response);

		verify(urlRedirectService, times(1)).generalRedirect(requestedURI);
		verifyNoMoreInteractions(urlRedirectService);
		verify(response, times(1)).sendRedirect(REDIRECT_MAPPING);
	}
}
