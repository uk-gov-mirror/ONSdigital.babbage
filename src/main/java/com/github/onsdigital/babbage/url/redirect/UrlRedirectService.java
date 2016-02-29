package com.github.onsdigital.babbage.url.redirect;

import au.com.bytecode.opencsv.CSVReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

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

	/* Property key for Data Explorer domain */
	private static final String DATA_EXPLORER_DOMAIN_KEY = "data_explorer_domain";

	/* Data explorer url format. */
	private static final String DATA_EXPLORER_URL_FORMAT = "{0}{1}";

	/* The Old taxonomy uri property key. */
	private static final String TAXONOMY_URI = "taxonomy_uri";

	/**
	 * Placeholders in order:
	 * <ul>
	 * <li>National Archive URL.</li>
	 * <li>National Archive timestamp (the latest crawl).</li>
	 * <li>The ONS base site URL.</li>
	 * <li>The requested uri that does not exist on the new site.</li>
	 * </ul>
	 */
	private static final String TNA_URL_FORMAT = "{0}/{1}/{2}{3}";

	/**
	 * Placeholders in order:
	 * <ul>
	 * <li>National Archive URL.</li>
	 * <li>National Archive timestamp (the latest crawl).</li>
	 * <li>The ONS base site URL.</li>
	 * <li>The old taxonomy URI.</li>
	 * <li>The NSCL parameter.</li>
	 * </ul>
	 */
	private static final String TNA_TAXONOMY_URI = "{0}/{1}/{2}{3}?nscl={4}";

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

	public String taxonomyRedirect(final String searchTerm) throws RedirectException {
		String redirect;
		if ((redirect = getTaxonomyMapping().get(searchTerm)) == null) {
			redirect = taxonomyNationalArchiveFormat(searchTerm);
		}
		return redirect;
	}

	public String generalRedirect(final String searchTerm) throws RedirectException {
		String redirect;
		if ((redirect = getGeneralMapping().get(searchTerm)) == null) {
			redirect = convertToNationalArchiveFormat(searchTerm);
		}
		return redirect;
	}

	public String dataExplorerRedirect(HttpServletRequest request) throws RedirectException {
		String requestedResource = request.getRequestURI();

		if (!request.getParameterMap().isEmpty()) {
			requestedResource += request.getQueryString();
		}

		return MessageFormat.format(DATA_EXPLORER_URL_FORMAT,
				urlRedirectPropertiesService.getProperty(DATA_EXPLORER_DOMAIN_KEY),
				requestedResource);
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

	private String convertToNationalArchiveFormat(String requestedURI) throws RedirectException {
		return MessageFormat.format(TNA_URL_FORMAT,
				urlRedirectPropertiesService.getProperty(NA_URL_KEY),
				urlRedirectPropertiesService.getProperty(NA_TIMESTAMP_KEY),
				urlRedirectPropertiesService.getProperty(ONS_DOMAIN_KEY),
				requestedURI);
	}

	private String taxonomyNationalArchiveFormat(final String nscl) throws RedirectException {
		return MessageFormat.format(TNA_TAXONOMY_URI,
				urlRedirectPropertiesService.getProperty(NA_URL_KEY),
				urlRedirectPropertiesService.getProperty(NA_TIMESTAMP_KEY),
				urlRedirectPropertiesService.getProperty(ONS_DOMAIN_KEY),
				urlRedirectPropertiesService.getProperty(TAXONOMY_URI),
				nscl);
	}
}
