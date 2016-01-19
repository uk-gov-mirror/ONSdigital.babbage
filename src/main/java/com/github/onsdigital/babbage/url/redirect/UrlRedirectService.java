package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.net.URL;

import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.MAPPING_IO_ERROR;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;

/**
 * Service provides functionality required for redirect URLs:
 * <ul>
 * <li>Finding the redirect value associated with the requested URL if a mapping exists.</li>
 * <li>Getting the ONS home page URL</li>
 * <li>Converting a requested URL to the National Archive format.</li>
 * <li>Converting a requested URL to the Data Explorer format.</li>
 * </ul>
 */
public class UrlRedirectService {

	private static final int MAPPING_LENGTH = 2;

	/* Property Key for TNA timestamp value. */
	private static final String NA_TIMESTAMP_KEY = "national_archive_timestamp";
	/* Property key for TNA url. */
	private static final String NA_URL_KEY = "national_archive_url";
	/* Property key for ONS domain. */
	private static final String ONS_DOMAIN_KEY = "ons_domain";
	/* National Archive URL format. */
	private static final String TNA_URL_FORMAT = "%s/%s/%s%s";
	/* Property key for Data Explorer domain */
	private static final String DATA_EXPLORER_DOMAIN_KEY = "data_explorer_domain";
	/* Data explorer url format. */
	private static final String DATA_EXPLORER_URL_FORMAT = "%s%s";
	/* Error message for Invalid Data Explorer requests. */
	private static final String INVALID_CATEGORY_DATA_EXPLORER_MSG = "Unable to convert %s to Data Explorer request." +
			" Identified RedirectCategory is not Data Explorer.";

	private UrlRedirectCSVFactory urlRedirectCSVFactory = new UrlRedirectCSVFactory();
	private UrlRedirectPropertiesService urlRedirectPropertiesService = new UrlRedirectPropertiesService();

	/**
	 * Returns the URL redirect value if one exists for the supplied {@link RedirectURL}. If no redirect mapping exists
	 * then null is returned.
	 *
	 * @param url the {@link RedirectURL} to find a redirect value for.
	 * @return the redirect value if one has been defined, null if no mapping has been defined.
	 * @throws RedirectException problem trying to find the redirect value.
	 */
	public String findRedirect(final RedirectURL url) throws RedirectException {
		try (CSVReader reader = urlRedirectCSVFactory.getReader(url.getCategory())) {
			String[] redirectPair;
			while ((redirectPair = reader.readNext()) != null) {
				String redirectKey = redirectPair[0];

				if (url.getSearchTerm().equalsIgnoreCase(redirectKey)) {
					if (redirectPair.length != MAPPING_LENGTH) {
						throw new RedirectException(MAPPING_IO_ERROR, null);
					}
					return redirectPair[1];
				}
			}
		} catch (IOException io) {
			throw new RedirectException(io, MAPPING_IO_ERROR, null);
		}
		return null;
	}

	/**
	 * Converts the {@link URL} to the National Archive format. Please note there is no guarantee that this resource
	 * exists on the national archive site.
	 *
	 * @param url the {@link RedirectURL} to convert to the National Archive format.
	 * @return the National Archive URL for the supplied URL.
	 */
	public String convertToNationalArchiveFormat(RedirectURL url) throws RedirectException {
		return String.format(TNA_URL_FORMAT,
				urlRedirectPropertiesService.getProperty(NA_URL_KEY),
				urlRedirectPropertiesService.getProperty(NA_TIMESTAMP_KEY),
				urlRedirectPropertiesService.getProperty(ONS_DOMAIN_KEY),
				url.getUrl().getFile());
	}

	public String convertToDataExplorerFormat(RedirectURL url) throws RedirectException {
		if (DATA_EXPLORER_REDIRECT.equals(url.getCategory())) {
			return String.format(DATA_EXPLORER_URL_FORMAT,
					urlRedirectPropertiesService.getProperty(DATA_EXPLORER_DOMAIN_KEY),
					url.getUrl().getPath());
		}
		throw new RedirectException(RedirectException.ErrorType.REDIRECT_URL_EXCEPTION,
				new Object[] {String.format(INVALID_CATEGORY_DATA_EXPLORER_MSG, url.getUrl().getPath())});
	}
}
