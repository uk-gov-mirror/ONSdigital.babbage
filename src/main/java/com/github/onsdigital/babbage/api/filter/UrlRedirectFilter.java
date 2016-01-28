package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;
import com.github.onsdigital.babbage.url.redirect.handler.impl.DataExplorerRedirectHandler;
import com.github.onsdigital.babbage.url.redirect.handler.impl.GeneralRedirectHandler;
import com.github.onsdigital.babbage.url.redirect.handler.impl.TaxonomyRedirectHandler;
import com.google.common.collect.ImmutableMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.DATA_EXPLORER_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.GENERAL_REDIRECT;
import static com.github.onsdigital.babbage.url.redirect.RedirectCategory.TAXONOMY_REDIRECT;

/**
 * Request Filter handling redirects for old/archived content. Only URL's where the URI starts with <b><i>/ons/...</i></b> will
 * be redirected all others will continue through the site without further processing. If a URL is identified as a redirect
 * (starting with '<i>/ons/...</i>') and no mapping is found then the request is redirected to the national archive site.
 */
public class UrlRedirectFilter implements Filter {

	private static final String ONS_URL_PREFIX = "/ons";

	private final ImmutableMap<RedirectCategory, RedirectHandler> handlers;

	private RedirectURL.Builder redirectUrlBuilder = new RedirectURL.Builder();

	public UrlRedirectFilter() {
		handlers = new ImmutableMap.Builder<RedirectCategory, RedirectHandler>()
				.put(TAXONOMY_REDIRECT, new TaxonomyRedirectHandler())
				.put(DATA_EXPLORER_REDIRECT, new DataExplorerRedirectHandler())
				.put(GENERAL_REDIRECT, new GeneralRedirectHandler())
				.build();
	}

	/**
	 * Determines if the request should be redirected. If so the Filter will look up the redirect value for the requested
	 * URL and redirect the request to that location. If no redirect is required then no further processing is carried
	 * out on the request.
	 *
	 * @param request  the {@link HttpServletRequest} to process.
	 * @param response the {@link HttpServletResponse} to use.
	 */
	public boolean filter(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (isRedirectCandidate(request)) {
				RedirectURL redirectURL = redirectUrlBuilder.build(request);
				String message = String.format("\tINFO: The requested resource %s has been moved. Request will be redirected.",
						redirectURL.getUrl().getFile());
				System.out.println(message);

				handlers.get(redirectURL.getCategory())
						.handle(redirectURL, response);
				return false;
			}
		} catch (RedirectException ex) {
			// TODO how should this be handled?
			ex.printStackTrace();
		}
		return true;
	}

	/**
	 * @return true if the request URI starts with '<i>/ons</i>' and the request parameters and headers do not contain
	 * a value for '<i>redirected</i>'. Return false otherwise.
	 */
	private boolean isRedirectCandidate(HttpServletRequest request) {
		return request.getRequestURI().toLowerCase().startsWith(ONS_URL_PREFIX);
	}
}
