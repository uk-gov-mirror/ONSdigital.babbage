package com.github.onsdigital.babbage.url.redirect;

import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test the RedirectProperties
 */
public class UrlRedirectPropertiesServiceTest {

	private UrlRedirectPropertiesService propertiesService;
	private Properties properties;

	@Before
	public void setUp() throws Exception {
		propertiesService = new UrlRedirectPropertiesService();

		properties = new Properties();
		properties.put("Venkman", "Peter");
		properties.put("Stantz", "Ray");
		properties.put("Egon", "Spengler");
		properties.put("Winston", "Zeddemore");
	}

	@After
	public void cleanUp() throws Exception {
		// Reset static value to null for next test.
		TestsUtil.setPrivateField(propertiesService, "REDIRECT_PROPERTIES", null);
	}

	@Test
	public void testGetPropertySuccess() throws Exception {
		TestsUtil.setPrivateField(propertiesService, "REDIRECT_PROPERTIES", properties);

		String result = propertiesService.getProperty("Venkman");

		assertThat("Incorrect value returned.", "Peter", equalTo(result));
	}

	/**
	 * Verifies {@link UrlRedirectPropertiesService#getProperty(String)} behaves as expected for scenario where the
	 * requested property is not found.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = RedirectException.class)
	public void testGetPropertyKeyDoesNotExist() throws Exception {
		TestsUtil.setPrivateField(propertiesService, "REDIRECT_PROPERTIES", properties);

		try {
			propertiesService.getProperty("slimer");
		} catch (RedirectException ex) {
			assertThat("Incorrect errorType test failed.", ex.getErrorType(),
					equalTo(RedirectException.ErrorType.PROPERTY_NOT_FOUND_ERROR));
			throw ex;
		}
	}

	/**
	 * Verifies {@link UrlRedirectPropertiesService#getProperty(String)} behaves as expected for scenario where the
	 * null is passed in as the property to search for.
	 *
	 * @throws Exception expected.
	 */
	@Test(expected = RedirectException.class)
	public void testGetPropertyKeyNull() throws RedirectException {
		try {
			propertiesService.getProperty(null);
		} catch (RedirectException ex) {
			assertThat("Incorrect errorType test failed.", ex.getErrorType(),
					equalTo(RedirectException.ErrorType.REQUIRED_PARAM_MISSING));
			throw ex;
		}
	}

	/**
	 * Tests scenario where the properties are loaded from the class path. Verifies the correct result is returned.
	 *
	 * @throws RedirectException unexpected test failed.
	 */
	@Test
	public void testGetPropertiesLoadResource() throws RedirectException {
		assertThat("Incorrect value returned.", propertiesService.getProperty("ons_domain"),
				equalTo("http://www.ons.gov.uk"));
	}
}
