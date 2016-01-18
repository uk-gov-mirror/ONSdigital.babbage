package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Objects.requireNonNull;

/**
 * Factory returns {@link CSVReader} pre-configured for the required URL redirect csv mapping file. The configuration of
 * the reader is determined by the {@link RedirectCategory} supplied by the calling class.
 */
public class UrlRedirectCSVFactory {

	private static final char DELIMITER = ',';
	private String TAXONOMY_REDIRECT_MAPPINGS = "/url-redirects/taxonomy-redirect-mapping.csv";
	private String GENERAL_REDIRECT_MAPPINGS = "/url-redirects/general-redirect-mapping.csv";

	/**
	 * Returns a {@link CSVReader} pre-configured for parsing the specified redirect type. The {@link RedirectCategory}
	 * parameter determines what configuration to use when creating the reader.
	 *
	 * @param category the {@link RedirectCategory} the reader is required for. Used to determine which configuration
	 *                    should be used when creating the {@link CSVReader}.
	 * @return the configured reader.
	 * @throws IOException unexpected error while attempting to configure the requested reader.
	 */
	public CSVReader getReader(RedirectCategory category) throws RedirectException {
		requireNonNull(category, "Category is a required parameter and cannot be null");
		switch (category) {
			case TAXONOMY_REDIRECT:
				return createInstance(TAXONOMY_REDIRECT_MAPPINGS);
			case GENERAL_REDIRECT:
				return createInstance(GENERAL_REDIRECT_MAPPINGS);
			default:
				throw new RedirectException(RedirectException.ErrorType.MAPPING_IO_ERROR, null);
		}
	}

	/**
	 * Creates a new {@link CSVReader} for the resource specified.
	 * @param resourcePath the resource the reader will be used to read.
	 *
	 * @return the reader.
	 * @throws IOException unexpected error while creating the CSVReader.
	 */
	private CSVReader createInstance(final String resourcePath) {
		InputStream in = UrlRedirectCSVFactory.class.getResourceAsStream(resourcePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return new CSVReader(reader, DELIMITER);
	}
}
