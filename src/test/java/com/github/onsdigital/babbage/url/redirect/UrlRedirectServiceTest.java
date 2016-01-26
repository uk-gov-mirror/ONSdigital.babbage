package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test verifies the {@link UrlRedirectService} behaves correctly for all positive and negative scenarios.
 */
public class UrlRedirectServiceTest {

	private static final String[] VALUE_ONE = {"daenerys", "targaryen"};
	private static final String[] VALUE_TWO = {"ned", "stark"};
	private static final String[] VALUE_THREE = {"tyrion", "lannister"};
	private static final String[] VALUE_FOUR = {"robert", "baratheon"};
	private static final String[] INVALID_CSV = {"first_value"};
	private static final String ONS_URL = "http://www.ons.gov.uk";

	@Mock
	private UrlRedirectCSVFactory csvFactoryMock;

	@Mock
	private UrlRedirectPropertiesService urlRedirectPropertiesServiceMock;

	@Mock
	private CSVReader csvReaderMock;

	@Mock
	private RedirectURL redirectURLMock;

	private UrlRedirectService service;

	@Before
	public void setUp() throws Exception {
		service = UrlRedirectService.getInstance();

		// Singleton instance so explicitly set these values to null for each test,
		TestsUtil.setPrivateField(service, "taxonomyMappings", null);
		TestsUtil.setPrivateField(service, "generalMappings", null);

		MockitoAnnotations.initMocks(this);
		TestsUtil.setPrivateField(service, "urlRedirectCSVFactory", csvFactoryMock);
		TestsUtil.setPrivateField(service, "urlRedirectPropertiesService", urlRedirectPropertiesServiceMock);

		when(csvReaderMock.readNext())
				.thenReturn(VALUE_ONE)
				.thenReturn(VALUE_TWO)
				.thenReturn(VALUE_THREE)
				.thenReturn(VALUE_FOUR)
				.thenReturn(null);
	}

	/**
	 * Test verifies the redirect service behaves correctly in the case where a valid taxonomy redirect that has a mapping.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFindRedirectTaxonomy() throws Exception {
		when(redirectURLMock.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURLMock.getSearchTerm())
				.thenReturn(VALUE_THREE[0]);
		when(csvFactoryMock.getReader(TAXONOMY_REDIRECT))
				.thenReturn(csvReaderMock);
		when(urlRedirectPropertiesServiceMock.getProperty(anyString()))
				.thenReturn("/house/");

		String result = service.findRedirect(redirectURLMock);

		assertThat("Incorrect redirect result.", result, equalTo(VALUE_THREE[1]));
		verify(csvFactoryMock, times(1)).getReader(TAXONOMY_REDIRECT);
		verifyZeroInteractions(urlRedirectPropertiesServiceMock);
		verify(csvReaderMock, atLeastOnce()).readNext();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFindRedirectGeneric() throws Exception {
		when(redirectURLMock.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		when(redirectURLMock.getSearchTerm())
				.thenReturn(VALUE_TWO[0]);
		when(csvFactoryMock.getReader(GENERAL_REDIRECT))
				.thenReturn(csvReaderMock);
		when(urlRedirectPropertiesServiceMock.getProperty(anyString()))
				.thenReturn("/house/");

		String result = service.findRedirect(redirectURLMock);

		assertThat("Incorrect redirect result.", result, equalTo(VALUE_TWO[1]));
		verify(csvFactoryMock, times(1)).getReader(GENERAL_REDIRECT);
		verifyZeroInteractions(urlRedirectPropertiesServiceMock);
		verify(csvReaderMock, atLeastOnce()).readNext();
	}

	@Test
	public void testConvertToDataExplorerFormatSuccess() throws Exception {
		URL requestedURL = new URL("http://www.ons.gov.uk/ons/data/dataset-finder?p_auth=JtIdMN9C&p_p" +
				"_id=FOLeftPanelSpring_WAR_FOLeftPanelportlet&p_p_lifecycle=1&p_p_state=normal&p_p_mode=" +
				"view&p_p_col_id=column-2&p_p_col_count=1&_FOLeftPanelSpring_WAR_FOLeftPanelportlet_javax.portlet." +
				"action=dFLPExpContNodeAction&filter=21:Demography:Demograffeg&nodeId=1&ctype=Standard&topicId=21");

		when(redirectURLMock.getUrl())
				.thenReturn(requestedURL);
		when(redirectURLMock.getCategory())
				.thenReturn(DATA_EXPLORER_REDIRECT);
		when(urlRedirectPropertiesServiceMock.getProperty("data_explorer_domain"))
				.thenReturn("data_explorer_domain");
		when(redirectURLMock.getOriginalRequestedResource())
				.thenReturn(requestedURL.getFile());

		String result = service.convertToDataExplorerFormat(redirectURLMock);
		String expected = "data_explorer_domain" + requestedURL.getFile();

		assertThat("Incorrect redirect value.", result, equalTo(expected));
		verify(urlRedirectPropertiesServiceMock, times(1)).getProperty("data_explorer_domain");
		verifyZeroInteractions(csvFactoryMock);
	}

	@Test(expected = RedirectException.class)
	public void testConvertToDataExplorerFormatSuccessInvalidCategory() throws Exception {
		URL requestedURL = new URL("http://www.ons.gov.uk/ons/taxonomy/index.html?some-param=true");

		when(redirectURLMock.getUrl())
				.thenReturn(requestedURL);
		when(redirectURLMock.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		try {
			service.convertToDataExplorerFormat(redirectURLMock);
		} catch (RedirectException ex) {
			assertThat("Unexpected ErrorType returned.",
					RedirectException.ErrorType.REDIRECT_URL_EXCEPTION, equalTo(ex.getErrorType()));
			verifyZeroInteractions(urlRedirectPropertiesServiceMock, csvFactoryMock);
			throw ex;
		}
	}

/*	*//**
	 * Test verifies behaviour is correct for case where the csv file is invalid.
	 *
	 * @throws Exception expected.
	 *//*
	@Test(expected = RedirectException.class)
	public void testFindRedirectMissingValue() throws Exception {
		when(csvReaderMock.readNext())
				.thenReturn(INVALID_CSV);
		when(redirectURLMock.getSearchTerm())
				.thenReturn(INVALID_CSV[0]);
		when(redirectURLMock.getCategory())
				.thenReturn(GENERAL_REDIRECT);
		when(csvFactoryMock.getReader(GENERAL_REDIRECT))
				.thenReturn(csvReaderMock);

		try {
			service.findRedirect(redirectURLMock);
		} catch (RedirectException ex) {
			assertThat("Invalid ErrorType.", ex.getErrorType(), equalTo(RedirectException.ErrorType.MAPPING_IO_ERROR));
			verify(csvFactoryMock, times(1)).getReader(GENERAL_REDIRECT);
			verify(csvReaderMock, atLeastOnce()).readNext();
			verifyZeroInteractions(urlRedirectPropertiesServiceMock);
			throw ex;
		}
	}*/

	/**
	 * Test verifies behaviour is correct for cases where no redirect mapping is found.
	 *
	 * @throws Exception unexpected test failed.
	 */
	@Test
	public void testFindRedirectNoResult() throws Exception {
		when(redirectURLMock.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURLMock.getSearchTerm())
				.thenReturn("Solid Snake");
		when(csvFactoryMock.getReader(TAXONOMY_REDIRECT))
				.thenReturn(csvReaderMock);

		String result = service.findRedirect(redirectURLMock);

		assertThat("Expected result is null.", result, equalTo(null));
		verify(csvFactoryMock, times(1)).getReader(TAXONOMY_REDIRECT);
		verify(csvReaderMock, times(5)).readNext();
		verifyZeroInteractions(urlRedirectPropertiesServiceMock);
	}

	/**
	 * Verify the behaviour for cases where {@link CSVReader#readNext()} throws an exception.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = RedirectException.class)
	public void testFindRedirectIOException() throws Exception {
		when(redirectURLMock.getCategory())
				.thenReturn(TAXONOMY_REDIRECT);
		when(redirectURLMock.getSearchTerm())
				.thenReturn(VALUE_THREE[0]);
		when(csvFactoryMock.getReader(TAXONOMY_REDIRECT))
				.thenReturn(csvReaderMock);
		when(csvReaderMock.readNext()).thenThrow(new IOException());

		try {
			service.findRedirect(redirectURLMock);
		} catch (RedirectException ex) {
			assertThat("Incorrect errorType test failed.", ex.getErrorType(),
					equalTo(RedirectException.ErrorType.MAPPING_IO_ERROR));
			throw ex;
		}
	}

	/**
	 * Test verifies {@link UrlRedirectService#convertToNationalArchiveFormat(RedirectURL)} behaves as expected for the
	 * happy path scenario.
	 *
	 * @throws Exception unexpected.
	 */
	@Test
	public void testConvertToNationalArchiveFormatSuccess() throws Exception {
		String requestedURL = ONS_URL + "/ons/taxonomy/index.html?nscl=Farm+Businesses";
		URL url = new URL(requestedURL);

		when(redirectURLMock.getUrl())
				.thenReturn(url);
		when(urlRedirectPropertiesServiceMock.getProperty("national_archive_url"))
				.thenReturn("national_archive_url");
		when(urlRedirectPropertiesServiceMock.getProperty("national_archive_timestamp"))
				.thenReturn("national_archive_timestamp");
		when(urlRedirectPropertiesServiceMock.getProperty("ons_domain"))
				.thenReturn("ons_domain");
		when(redirectURLMock.getOriginalRequestedResource())
				.thenReturn(url.getFile());

		String result = service.convertToNationalArchiveFormat(redirectURLMock);
		String expected = "national_archive_url/national_archive_timestamp/ons_domain/ons/taxonomy/index.html?nscl=Farm+Businesses";

		assertThat("Incorrect result test failed.", result, equalTo(expected));
		verify(urlRedirectPropertiesServiceMock, times(1)).getProperty("national_archive_url");
		verify(urlRedirectPropertiesServiceMock, times(1)).getProperty("national_archive_timestamp");
		verify(urlRedirectPropertiesServiceMock, times(1)).getProperty("ons_domain");
		verifyNoMoreInteractions(urlRedirectPropertiesServiceMock);
	}
}
