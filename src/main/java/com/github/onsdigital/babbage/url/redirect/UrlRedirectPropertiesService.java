package com.github.onsdigital.babbage.url.redirect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.PROPERTIES_LOAD_ERROR;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.PROPERTY_NOT_FOUND_ERROR;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REQUIRED_PARAM_MISSING;

/**
 * Service for obtaining property values relating to URL Redirects.
 */
public class UrlRedirectPropertiesService {

	private static final String REDIRECT_PROPERTIES_SOURCE = "/url-redirects/url-redirect-mapping.properties";
	private static Properties REDIRECT_PROPERTIES;

	/**
	 * Gets the value associated with the specified key as a {@link String} if it exists. If the requested property does
	 * not exist a {@link NullPointerException} is thrown.
	 *
	 * @param key the name of the property to get. <b>Null not accepted.</b>
	 * @return the {@link String} value for the requested key if it exists. If no value is found a
	 * {@link NullPointerException} is thrown.
	 * @throws RedirectException an unexpected issue while attempting to find the requested property.
	 */
	public String getProperty(String key) throws RedirectException {
		if (key == null) {
			throw new RedirectException(REQUIRED_PARAM_MISSING, new Object[] {"key"});
		}
		if (REDIRECT_PROPERTIES == null) {
			loadProperties();
		}
		if (REDIRECT_PROPERTIES.containsKey(key)) {
			return String.valueOf(REDIRECT_PROPERTIES.get(key));
		}
		throw new RedirectException(PROPERTY_NOT_FOUND_ERROR, new Object[]{key});
	}

	private void loadProperties() throws RedirectException {
		try (InputStream in = UrlRedirectPropertiesService.class.getResourceAsStream(REDIRECT_PROPERTIES_SOURCE)) {
			REDIRECT_PROPERTIES = new Properties();
			REDIRECT_PROPERTIES.load(in);
		} catch (IOException io) {
			throw new RedirectException(io, PROPERTIES_LOAD_ERROR);
		}
	}
}
