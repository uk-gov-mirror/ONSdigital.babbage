package com.github.onsdigital.babbage.url.redirect;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test verifies {@link RedirectCategory#getCategoryFromURI(HttpServletRequest)} behaves correctly in all positive and
 * negative scenarios.
 */
public class RedirectCategoryTest {

	@Mock
	private HttpServletRequest mocRequest;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Verify the correct {@link RedirectCategory} is returned when a {@link HttpServletRequest} with a valid taxonomy
	 * redirect URI is passed in.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testGetCategoryForTaxonomyRedirectURL() throws Exception {
		when(mocRequest.getRequestURI()).thenReturn("/ons/taxonomy/index.html");

		assertThat("Incorrect RedirectCategory returned.", RedirectCategory.getCategoryFromURI(mocRequest),
				equalTo(RedirectCategory.TAXONOMY_REDIRECT));
	}

	/**
	 * Verify the correct {@link RedirectCategory} is returned when a {@link HttpServletRequest} with a valid non-taxonomy
	 * redirect URI is passed in.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testGetCategoryForGenericRedirectURL() throws Exception {
		when(mocRequest.getRequestURI()).thenReturn("/ons/publications/index.html");

		assertThat("Incorrect RedirectCategory returned.", RedirectCategory.getCategoryFromURI(mocRequest),
				equalTo(RedirectCategory.GENERAL_REDIRECT));
	}

	@Test
	public void testGetCategoryForDataExplorerRedirectURL() throws Exception {
		String uri = "/ons/data/dataset-finder?p_auth=JtIdMN9C&p_p_id=FOLeftPanelSpring_WAR_FOLeftPanelportlet&" +
				"p_p_lifecycle=1&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_count=1&_FOLeftPanelSpring_" +
				"WAR_FOLeftPanelportlet_javax.portlet.action=dFLPExpContNodeAction&filter=21:Demography:" +
				"Demograffeg&nodeId=1&ctype=Standard&topicId=21";
		when(mocRequest.getRequestURI()).thenReturn(uri);

		assertThat("Incorrect RedirectCatgeory returned.", RedirectCategory.getCategoryFromURI(mocRequest),
				equalTo(RedirectCategory.DATA_EXPLORER_REDIRECT));
	}

	/**
	 * Verify a {@link RedirectException} with the correct {@link RedirectException.ErrorType} is throw when a
	 * {@link HttpServletRequest} with a non redirect URI is passed in.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = RedirectException.class)
	public void testGetCategoryForNonRedirectURL() throws Exception {
		when(mocRequest.getRequestURI()).thenReturn("/businessindustryandtrade");

		try {
			RedirectCategory.getCategoryFromURI(mocRequest);
		} catch (RedirectException ex) {

			assertThat("Unexpected ErrorType.", ex.getErrorType(),
					equalTo(RedirectException.ErrorType.UNKNOWN_REDIRECT_CATEGORY));
			throw ex;
		}
	}

	/**
	 * Test verifies that {@link RedirectCategory#getParameterName()} returns the expected result for
	 * {@link RedirectCategory#TAXONOMY_REDIRECT} and {@link RedirectCategory#GENERAL_REDIRECT}.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testGetParameterName() throws Exception {
		assertThat("Incorrect parameter name returned for Taxonomy Request category.",
				RedirectCategory.TAXONOMY_REDIRECT.getParameterName(), equalTo("nscl"));

		assertThat("Incorrect parameter name returned for Non Taxonomy Request category.",
				RedirectCategory.GENERAL_REDIRECT.getParameterName(), equalTo(null));
	}
}
