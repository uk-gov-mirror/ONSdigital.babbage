package com.github.onsdigital.babbage.url.redirect;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by dave on 12/21/15.
 */
public class RedirectURLTest {

	private static final String REQUEST_URI = "/ons/taxonomy/index.html";
	private static final String BASE_URL_STRING = "http://www.ons.gov.uk" + REQUEST_URI;
	private static final String TAXONOMY_PARAM = RedirectCategory.TAXONOMY_REDIRECT.getParameterName();

	@Mock
	private HttpServletRequest mockRequest;

	private RedirectURL result;
	private Map<String, String[]> requestParams;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		requestParams = new HashMap<>();
	}

	/**
	 * Test verifies the expected {@link RedirectURL} is created for a taxonomy url with no parameter.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testTaxonomyURLNoParam() throws Exception {
		when(mockRequest.getRequestURL()).thenReturn(new StringBuffer(BASE_URL_STRING));
		when(mockRequest.getRequestURI()).thenReturn(REQUEST_URI);

		result = new RedirectURL.Builder().build(mockRequest);

		assertThat("Incorrect toString value.", result.toString(), equalTo(BASE_URL_STRING));
		assertThat("Incorrect category.", result.getCategory(), equalTo(TAXONOMY_REDIRECT));
		assertThat("Incorrect 'contains parameter' result.", result.containsParameter(TAXONOMY_PARAM), is(false));
		assertThat("Incorrect parameter value", result.getParameter(), equalTo(null));
	}

	/**
	 * Test verifies the expected {@link RedirectURL} is created from a taxonomy url containing the nscl parameter.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testTaxonomyURLWithParamOnly() throws Exception {
		when(mockRequest.getRequestURL()).thenReturn(new StringBuffer(BASE_URL_STRING));
		when(mockRequest.getRequestURI()).thenReturn(REQUEST_URI);

		requestParams.put("NSCL", new String[]{"Farming Methods"});
		when(mockRequest.getParameterMap()).thenReturn(requestParams);

		result = new RedirectURL.Builder().build(mockRequest);

		assertThat("Incorrect toString value.", result.toString(), equalTo(BASE_URL_STRING + "?nscl=" + URLEncoder.encode("farming+methods")));
		assertThat("Incorrect category.", result.getCategory(), equalTo(TAXONOMY_REDIRECT));
		assertThat("Incorrect 'contains parameter' result.", result.containsParameter(TAXONOMY_PARAM), is(true));
		assertThat("Incorrect parameter value", result.getParameter(), equalTo("farming+methods"));
	}

	/**
	 * Test verifies the expected {@link RedirectURL} is created from a taxonomy url containing the nscl parameter as
	 * well as several others.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testTaxonomyURLMultipleParams() throws Exception {
		when(mockRequest.getRequestURL()).thenReturn(new StringBuffer(BASE_URL_STRING));
		when(mockRequest.getRequestURI()).thenReturn(REQUEST_URI);

		requestParams.put("param1", new String[]{"Ray Stantz"});
		requestParams.put("param2", new String[]{"Peter Venkman"});
		requestParams.put("NSCL", new String[]{"Farming Methods"});
		requestParams.put("param3", new String[]{"Egon Spengler"});
		requestParams.put("param4", new String[]{"Winston Zeddemore"});
		when(mockRequest.getParameterMap()).thenReturn(requestParams);

		result = new RedirectURL.Builder().build(mockRequest);

		assertThat("Incorrect toString value.", result.toString(), equalTo(BASE_URL_STRING + "?nscl=" + URLEncoder.encode("farming+methods")));
		assertThat("Incorrect category.", result.getCategory(), equalTo(TAXONOMY_REDIRECT));
		assertThat("Incorrect 'contains parameter' result.", result.containsParameter(TAXONOMY_PARAM), is(true));
		assertThat("Incorrect parameter value", result.getParameter(), equalTo("farming+methods"));
	}
}
