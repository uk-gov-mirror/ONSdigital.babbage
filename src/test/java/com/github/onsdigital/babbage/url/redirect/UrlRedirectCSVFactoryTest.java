package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Before;
import org.junit.Test;

import static com.github.onsdigital.babbage.util.TestsUtil.setPrivateField;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UrlRedirectCSVFactoryTest {

	private UrlRedirectCSVFactory csvFactory;

	@Before
	public void setUp() throws Exception {
		csvFactory = new UrlRedirectCSVFactory();
	}

	@Test
	public void testGetTaxonomyReader() throws Exception {
		setPrivateField(csvFactory, "TAXONOMY_REDIRECT_MAPPINGS", "/url-redirects/taxonomy-redirect-mapping.csv");
		String[] expected = {"taxonomy+redirect", "/taxonomy/redirect"};

		try (CSVReader reader = csvFactory.getReader(RedirectCategory.TAXONOMY_REDIRECT)) {
			assertThat("CSVReader should not be null.", reader, notNullValue());
			String[] line = reader.readNext();
			assertThat("CSVReader.readLine should not return null.", line, notNullValue());
			assertThat("CSVReader.readLine returned unexpected value.", line, equalTo(expected));
		}
	}

	@Test
	public void testGetNonTaxonomyReader() throws Exception {
		setPrivateField(csvFactory, "GENERAL_REDIRECT_MAPPINGS", "/url-redirects/general-redirect-mapping.csv");
		String[] expected = {"non+taxonomy+redirect", "/non/taxonomy/redirect"};

		try (CSVReader reader = csvFactory.getReader(RedirectCategory.GENERAL_REDIRECT)) {
			assertThat("CSVReader should not be null.", reader, notNullValue());
			String[] line = reader.readNext();
			assertThat("CSVReader.readLine should not return null.", line, notNullValue());
			assertThat("CSVReader.readLine returned unexpected value.", line, equalTo(expected));
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetReaderCategoryNull() throws Exception {
		csvFactory.getReader(null);
	}
}
