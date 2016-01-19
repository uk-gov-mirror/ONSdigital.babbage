package com.github.onsdigital.babbage.url.redirect;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REDIRECT_URL_EXCEPTION;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REQUIRED_PARAM_MISSING;

/**
 * POJO representing a Redirect URL. Provides methods for accessing parts of the URL when processing the redirect.
 */
public class RedirectURL {

	private final URL url;
	private final boolean containsParameter;
	private final StringBuilder urlString;
	private String parameterValue = null;
	private final RedirectCategory category;

	private RedirectURL(HttpServletRequest request) throws RedirectException {
		this.urlString = new StringBuilder(request.getRequestURL());
		this.category = RedirectCategory.getCategoryFromURI(request);

		Map<String, String[]> sanitiseParameters = sanitiseParameterKeys(request.getParameterMap());
		this.containsParameter = containsParameter(sanitiseParameters, getParameterName());

		if (containsParameter) {
			this.urlString.append("?" + category.getParameterName() + "=");
			this.parameterValue = getFormattedParam(sanitiseParameters, getParameterName());
			this.urlString.append(parameterValue);
		}
		try {
			this.url = new URL(urlString.toString());
		} catch (MalformedURLException ex) {
			throw new RedirectException(ex, REDIRECT_URL_EXCEPTION);
		}
	}

	/**
	 * @return true if the {@link HttpServletRequest} contains the required parameter for this type of redirect.
	 */
	public boolean containsParameter() {
		return this.containsParameter;
	}

	/**
	 * @return the name of the key request parameter for this type of redirect.
	 */
	public String getParameterName() {
		return this.category.getParameterName();
	}

	/**
	 * @return the key parameter value.
	 */
	public String getParameter() {
		return this.parameterValue;
	}

	/**
	 * @return the {@link RedirectURL} as a {@link URL}.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * @return the {@link RedirectCategory} for this redirect url.
	 */
	public RedirectCategory getCategory() {
		return this.category;
	}

	/**
	 * @return the term to use while searching for the redirect mapping for this redirect.
	 */
	public String getSearchTerm() {
		if (TAXONOMY_REDIRECT.equals(this.category)) {
			return parameterValue;
		}
		return this.url.getPath().toLowerCase();
	}

	private Map<String, String[]> sanitiseParameterKeys(Map<String, String[]> params) {
		Map<String, String[]> sanitisedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		sanitisedParams.putAll(params);
		return sanitisedParams;
	}

	private boolean containsParameter(Map<String, String[]> params, final String name) {
		return !params.isEmpty() && params.containsKey(name);
	}

	private String getFormattedParam(Map<String, String[]> params, final String name) {
		return URLEncoder.encode(params.get(name)[0]).toLowerCase();
	}

	@Override
	public String toString() {
		return this.url.toString();
	}

	/**
	 * Builder for creating {@link RedirectURL} objects.
	 */
	public static class Builder {
		/**2a3
		 * Build a new {@link RedirectURL}.
		 *
		 * @param request the {@link HttpServletRequest} to use when building the {@link RedirectURL}. <b>Cannot be null.</b>
		 * @return a {@link RedirectURL} generated from the supplied {@link HttpServletRequest}.
		 * @throws RedirectException unexpected problem while creating RedirectURL.
		 */
		public RedirectURL build(HttpServletRequest request) throws RedirectException {
			if (request == null) {
				throw new RedirectException(REQUIRED_PARAM_MISSING, new Object[]{"RedirectURL.Builder.request"});
			}
			return new RedirectURL(request);
		}
	}

}
