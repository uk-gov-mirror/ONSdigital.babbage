package com.github.onsdigital.babbage.url.redirect;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Defines constants for each type of redirects.
 */
public enum RedirectCategory {

	/**
	 * Category representing <i>taxonomy redirects</i> i.e URI's starting with <i>/ons/taxonomy/</i>.
	 */
	TAXONOMY_REDIRECT,

	/**
	 * Category representing <i>Data Explorer</i> redirects. i.e. URI's starting with <i>/ons/data/</i>
	 */
	DATA_EXPLORER_REDIRECT,

	/**
	 * Category for non <i>taxonomy</i> and non <i>data explorer</i> redirects i.e. URI's starting with <i>/ons/</i> but not
	 * <i>/ons/taxonomy/</i> or <i>/ons/data/</i>.
	 */
	GENERAL_REDIRECT;

	private static final String TAXONOMY_URI = "/ons/taxonomy";
	private static final String DATA_EXPLORER_URI = "/ons/data";
	private static final String GENERAL_URI = "/ons/";

	public static Optional<RedirectCategory> categorize(HttpServletRequest request) {
		requireNonNull(request, "request is a required parameter and cannot be null");
		String requestURI = request.getRequestURI().toLowerCase();

		if (requestURI.startsWith(TAXONOMY_URI)) {
			return Optional.of(TAXONOMY_REDIRECT);
		}
		if (requestURI.equalsIgnoreCase(DATA_EXPLORER_URI) || requestURI.startsWith(DATA_EXPLORER_URI + "/")) {
			return Optional.of(DATA_EXPLORER_REDIRECT);
		}
		if (requestURI.startsWith(GENERAL_URI)) {
			return Optional.of(GENERAL_REDIRECT);
		}
		return Optional.empty();
	}

}
