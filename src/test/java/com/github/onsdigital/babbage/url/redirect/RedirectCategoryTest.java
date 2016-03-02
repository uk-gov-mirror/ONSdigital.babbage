package com.github.onsdigital.babbage.url.redirect;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test verifies {@link RedirectCategory#categorize(HttpServletRequest)} behaves correctly in all positive and
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

		assertThat("Incorrect RedirectCategory returned.", RedirectCategory.categorize(mocRequest),
				equalTo(Optional.of(RedirectCategory.TAXONOMY_REDIRECT)));
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

		assertThat("Incorrect RedirectCategory returned.", RedirectCategory.categorize(mocRequest),
				equalTo(Optional.of(RedirectCategory.GENERAL_REDIRECT)));
	}

	@Test
	public void testGetCategoryForDataExplorerRedirectURL() throws Exception {
		String uri = "/ons/data/dataset-finder?p_auth=JtIdMN9C&p_p_id=FOLeftPanelSpring_WAR_FOLeftPanelportlet&" +
				"p_p_lifecycle=1&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_count=1&_FOLeftPanelSpring_" +
				"WAR_FOLeftPanelportlet_javax.portlet.action=dFLPExpContNodeAction&filter=21:Demography:" +
				"Demograffeg&nodeId=1&ctype=Standard&topicId=21";
		when(mocRequest.getRequestURI()).thenReturn(uri);

		assertThat("Incorrect RedirectCatgeory returned.", RedirectCategory.categorize(mocRequest),
				equalTo(Optional.of(RedirectCategory.DATA_EXPLORER_REDIRECT)));
	}

	/**
	 * Verify a {@link RedirectException} with the correct {@link RedirectException.ErrorType} is throw when a
	 * {@link HttpServletRequest} with a non redirect URI is passed in.
	 *
	 * @throws Exception expected.
	 */
	@Test
	public void testGetCategoryForNonRedirectURL() throws Exception {
		when(mocRequest.getRequestURI())
				.thenReturn("/businessindustryandtrade");

		Optional<RedirectCategory> category = RedirectCategory.categorize(mocRequest);

		assertThat("Unexpected Optional.empty().", category,
				equalTo(Optional.empty()));
	}
}
