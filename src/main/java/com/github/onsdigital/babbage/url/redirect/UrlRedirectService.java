package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.MAPPING_IO_ERROR;

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

	private static UrlRedirectService instance = null;

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

	private final UrlRedirectCSVFactory urlRedirectCSVFactory;
	private final UrlRedirectPropertiesService urlRedirectPropertiesService;
	private Map<String, String> taxonomyMappings;
	private Map<String, String> generalMappings;

	/**
	 * @return singleton instance of the {@link UrlRedirectService}.
	 */
	public static UrlRedirectService getInstance() {
		if (instance == null) {
			instance = new UrlRedirectService();
		}
		return instance;
	}

	/**
	 * Constructs a new Services object and initialises the fields.
	 */
	private UrlRedirectService() {
		this.urlRedirectCSVFactory = new UrlRedirectCSVFactory();
		this.urlRedirectPropertiesService = new UrlRedirectPropertiesService();
		this.taxonomyMappings = null;
		this.generalMappings = null;
	}

	/**
	 * Returns the URL redirect value if one exists for the supplied {@link RedirectURL}. If no redirect mapping exists
	 * then null is returned.
	 *
	 * @param url the {@link RedirectURL} to find a redirect value for.
	 * @return the redirect value if one has been defined, null if no mapping has been defined.
	 * @throws RedirectException problem trying to find the redirect value.
	 */
	public String findRedirect(final RedirectURL url) throws RedirectException {
		RedirectCategory category = url.getCategory();
		Map<String, String> mapping = null;

		switch (category) {
			case TAXONOMY_REDIRECT:
				mapping = getTaxonomyMapping();
				break;
			case GENERAL_REDIRECT:
				mapping = getGeneralMapping();
				break;
		}
		return mapping != null ? mapping.get(url.getSearchTerm()) : null;
	}

	/**
	 * Get a populated Taxonomy Redirect Mapping Map.
	 */
	private Map<String, String> getTaxonomyMapping() throws RedirectException {
		if (taxonomyMappings == null) {
			this.taxonomyMappings = loadMappings(TAXONOMY_REDIRECT);
		}
		return taxonomyMappings;
	}

	/**
	 * Get a populated General Redirect Mapping Map.
	 */
	private Map<String, String> getGeneralMapping() throws RedirectException {
		if (generalMappings == null) {
			this.generalMappings = loadMappings(GENERAL_REDIRECT);
		}
		return generalMappings;
	}

	/**
	 * Load the Redirect Mappings into Memory from the csv file.
	 *
	 * @param category the {@link RedirectCategory} to load the mapping for.
	 */
	private Map<String, String> loadMappings(RedirectCategory category) throws RedirectException {
		try (CSVReader reader = urlRedirectCSVFactory.getReader(category)) {
			Map<String, String> mappings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			String[] redirectPair;
			while ((redirectPair = reader.readNext()) != null) {
				if (redirectPair.length != MAPPING_LENGTH) {
					throw new RedirectException(MAPPING_IO_ERROR, null);
				}
				mappings.put(redirectPair[0], redirectPair[1]);
			}
			return mappings;
		} catch (IOException io) {
			throw new RedirectException(io, MAPPING_IO_ERROR, null);
		}
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
				url.getOriginalRequestedResource());
	}

	/**
	 * Convert the Requested URL to the Data Explorer redirect format.
	 *
	 * @param url the {@link RedirectURL} to convert.
	 * @return the converted value for the original url.
	 * @throws RedirectException problem converting the url.
	 */
	public String convertToDataExplorerFormat(RedirectURL url) throws RedirectException {
		if (DATA_EXPLORER_REDIRECT.equals(url.getCategory())) {
			return String.format(DATA_EXPLORER_URL_FORMAT,
					urlRedirectPropertiesService.getProperty(DATA_EXPLORER_DOMAIN_KEY),
					url.getOriginalRequestedResource());
		}
		throw new RedirectException(RedirectException.ErrorType.REDIRECT_URL_EXCEPTION,
				new Object[]{String.format(INVALID_CATEGORY_DATA_EXPLORER_MSG, url.getUrl().getPath())});
	}
}
