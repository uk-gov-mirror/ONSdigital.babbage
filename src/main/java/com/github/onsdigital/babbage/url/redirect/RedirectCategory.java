package com.github.onsdigital.babbage.url.redirect;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.UNKNOWN_REDIRECT_CATEGORY;
import static java.util.Objects.requireNonNull;

/**
 * Defines constants for each type of redirects.
 */
public enum RedirectCategory {

	/**
	 * Category representing <i>taxonomy redirects</i> i.e URI's starting with <i>/ons/taxonomy/</i>.
	 */
	TAXONOMY_REDIRECT("nscl"),

	/**
	 * Category representing <i>Data Explorer</i> redirects. i.e. URI's starting with <i>/ons/data/</i>
	 */
	DATA_EXPLORER_REDIRECT(),

	/**
	 * Category for non <i>taxonomy</i> and non <i>data explorer</i> redirects i.e. URI's starting with <i>/ons/</i> but not
	 * <i>/ons/taxonomy/</i> or <i>/ons/data/</i>.
	 */
	GENERAL_REDIRECT();

	private static final String TAXONOMY_URI = "/ons/taxonomy";
	private static final String DATA_EXPLORER_URI = "/ons/data/";
	private static final String GENERAL_URI = "/ons/";

	private final String parameter;

	RedirectCategory(String parameter) {
		this.parameter = parameter;
	}

	RedirectCategory() {
		this.parameter = null;
	}

	public String getParameterName() {
		return this.parameter;
	}

	/**
	 * Helper method to determine the {@link RedirectCategory} from the {@link HttpServletRequest}.
	 *
	 * @param request the {@link HttpServletRequest} to use to determine the {@link RedirectCategory}.
	 * @return the {@link RedirectCategory} for the requested URL.
	 * @throws RedirectException thrown if not able to determine {@link RedirectCategory} i.e. the requested URL is not
	 *                           a redirect.
	 */
	public static RedirectCategory getCategoryFromURI(HttpServletRequest request) throws RedirectException {
		requireNonNull(request, "request is a required parameter and cannot be null");
		String requestURI = request.getRequestURI().toLowerCase();
		if (requestURI.startsWith(TAXONOMY_URI)) {
			return TAXONOMY_REDIRECT;
		}
		if (requestURI.startsWith(DATA_EXPLORER_URI)) {
			return DATA_EXPLORER_REDIRECT;
		}
		if (requestURI.startsWith(GENERAL_URI)) {
			return GENERAL_REDIRECT;
		}

		throw new RedirectException(UNKNOWN_REDIRECT_CATEGORY, new Object[]{requestURI});
	}

}
