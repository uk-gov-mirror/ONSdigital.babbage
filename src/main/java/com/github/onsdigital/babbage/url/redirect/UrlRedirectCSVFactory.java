package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;
import com.github.onsdigital.babbage.url.AbstractCSVFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Factory returns {@link CSVReader} pre-configured for the required URL redirect csv mapping file. The configuration of
 * the reader is determined by the {@link RedirectCategory} supplied by the calling class.
 */
public class UrlRedirectCSVFactory extends AbstractCSVFactory {

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
}
