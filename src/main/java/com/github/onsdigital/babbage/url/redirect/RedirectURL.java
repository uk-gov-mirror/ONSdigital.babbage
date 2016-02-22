package com.github.onsdigital.babbage.url.redirect;

import org.apache.http.client.utils.URIBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REDIRECT_URL_EXCEPTION;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REQUIRED_PARAM_MISSING;

/**
 * POJO representing a Redirect URL. Provides methods for accessing parts of the URL when processing the redirect.
 */
public class RedirectURL {

	private final URL url;
	private String parameterValue = null;
	private final RedirectCategory category;
	private final HttpServletRequest originalRequest;

	private RedirectURL(HttpServletRequest request) throws RedirectException {
		this.originalRequest = request;
		this.category = RedirectCategory.getCategoryFromURI(originalRequest);

		try {
			String parsedRequestedURLString;

			switch (category) {
				case TAXONOMY_REDIRECT:
					/**
					 * Taxonomy redirects only require the URI and nscl param - the rest is ignored for the lookup.
					 */
					URIBuilder uriBuilder = new URIBuilder(originalRequest.getRequestURL().toString());

					if (containsParameter(TAXONOMY_REDIRECT.getParameterName())) {
						this.parameterValue = getFormattedParam(getParameterName());
						uriBuilder.addParameter(category.getParameterName(), this.parameterValue);
					}
					parsedRequestedURLString = uriBuilder.build().toString();
					break;
				case DATA_EXPLORER_REDIRECT:
					/**
					 * Data explorer redirects simply change the domain name of the original request - the URI and
					 * param String is does not change.
					 */
					parsedRequestedURLString = originalRequest.getRequestURL().toString();
					break;
				default:
					/**
					 * General Redirects - only requires onyl the original URI for the lookup. The Query string is ignored.
					 */
					parsedRequestedURLString = originalRequest.getRequestURL().toString();
			}

			this.url = new URL(parsedRequestedURLString);
		} catch (MalformedURLException | URISyntaxException ex) {
			throw new RedirectException(ex, REDIRECT_URL_EXCEPTION);
		}
	}

	/**
	 * @return true if the {@link HttpServletRequest} contains the required parameter for this type of redirect.
	 */
	public boolean containsParameter(String param) {
		return this.originalRequest.getParameterMap()
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().equalsIgnoreCase(param))
				.findFirst()
				.isPresent();
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

	public boolean hasParameters() {
		return !originalRequest.getParameterMap().isEmpty();
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

	private String getFormattedParam(final String name) {

		Optional<Map.Entry<String, String[]>> value  = originalRequest.getParameterMap().entrySet().stream()
				.filter(entry -> entry.getKey().equalsIgnoreCase(name))
				.findFirst();
		if (value.isPresent()) {
			return URLEncoder.encode(value.get().getValue()[0].toLowerCase());
		}
		return null;
	}

	/**
	 * @return the URI & Query string that was originally requested.
	 * @throws RedirectException
	 */
	public String getOriginalRequestedResource() throws RedirectException {
		try {
			URIBuilder uriBuilder = new URIBuilder(originalRequest.getRequestURI());
			if (!originalRequest.getParameterMap().isEmpty()) {
				originalRequest.getParameterMap().entrySet().forEach(entry -> {
					uriBuilder.addParameter(entry.getKey().toLowerCase(), entry.getValue()[0].toLowerCase());
				});
			}
			return uriBuilder.toString();
		} catch (Exception ex) {
			throw new RedirectException(REDIRECT_URL_EXCEPTION, new Object[]{ex});
		}
	}

	@Override
	public String toString() {
		return this.url.toString();
	}

	/**
	 * Builder for creating {@link RedirectURL} objects.
	 */
	public static class Builder {
		/**
		 * 2a3
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
