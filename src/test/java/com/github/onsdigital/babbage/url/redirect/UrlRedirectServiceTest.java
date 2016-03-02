package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
	private static final String NA_TIMESTAMP_KEY = "national_archive_timestamp";
	private static final String NA_URL_KEY = "national_archive_url";
	private static final String ONS_DOMAIN_KEY = "ons_domain";
	private static final String DATA_EXPLORER_DOMAIN_KEY = "data_explorer_domain";
	private static final String DATA_EXPLORER_URL_FORMAT = "{0}{1}";
	private static final String TAXONOMY_URI = "taxonomy_uri";


	@Mock
	private UrlRedirectCSVFactory csvFactoryMock;

	@Mock
	private UrlRedirectPropertiesService urlRedirectPropertiesServiceMock;

	@Mock
	private CSVReader csvReaderMock;

	private UrlRedirectService service;
	private String searchTerm;
	private String tnaUrl;

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

		when(urlRedirectPropertiesServiceMock.getProperty(NA_URL_KEY))
				.thenReturn(NA_URL_KEY);
		when(urlRedirectPropertiesServiceMock.getProperty(NA_TIMESTAMP_KEY))
				.thenReturn(NA_TIMESTAMP_KEY);
		when(urlRedirectPropertiesServiceMock.getProperty(ONS_DOMAIN_KEY))
				.thenReturn(ONS_DOMAIN_KEY);
		when(urlRedirectPropertiesServiceMock.getProperty(TAXONOMY_URI))
				.thenReturn(TAXONOMY_URI);

		tnaUrl = new StringBuilder(NA_URL_KEY)
				.append("/")
				.append(NA_TIMESTAMP_KEY)
				.append("/")
				.append(ONS_DOMAIN_KEY)
				.append(TAXONOMY_URI)
				.append("?nscl=")
				.toString();
	}

	@Test
	public void shouldFindTaxonomyRedirectMapping() throws Exception {
		searchTerm = "daenerys";
		when(csvFactoryMock.getReader(TAXONOMY_REDIRECT))
				.thenReturn(csvReaderMock);
		when(urlRedirectPropertiesServiceMock.getProperty(anyString()))
				.thenReturn("/house/");

		String result = service.taxonomyRedirect(searchTerm);

		assertThat("Incorrect redirect result.", result, equalTo(VALUE_ONE[1]));
		verify(csvFactoryMock, times(1)).getReader(TAXONOMY_REDIRECT);
		verifyZeroInteractions(urlRedirectPropertiesServiceMock);
		verify(csvReaderMock, atLeastOnce()).readNext();
	}

	@Test
	public void shouldRedirectTaxonomyToNationArchive() throws Exception {
		searchTerm = "bob";
		when(csvFactoryMock.getReader(TAXONOMY_REDIRECT))
				.thenReturn(csvReaderMock);

		String result = service.taxonomyRedirect(searchTerm);

		assertThat("Incorrect redirect result.", result, equalTo(tnaUrl + searchTerm));
		verify(csvFactoryMock, times(1)).getReader(TAXONOMY_REDIRECT);
		verify(urlRedirectPropertiesServiceMock, times(4)).getProperty(anyString());
		verify(csvReaderMock, atLeastOnce()).readNext();
	}

	// TODO increase test coverage.

}
