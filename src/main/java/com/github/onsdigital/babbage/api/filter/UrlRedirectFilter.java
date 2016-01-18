package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request Filter handling redirects for old/archived content. Only URL's where the URI starts with <b><i>/ons/...</i></b> will
 * be redirected all others will continue through the site without further processing. If a URL is identified as a redirect
 * (starting with '<i>/ons/...</i>') and no mapping is found then the request is redirected to the national archive site.
 */
public class UrlRedirectFilter implements Filter {

	private static final String ONS_URL_PREFIX = "/ons";

	private UrlRedirectService urlRedirectService = new UrlRedirectService();
	private RedirectURL.Builder redirectUrlBuilder = new RedirectURL.Builder();

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
			String requestURI = request.getRequestURI();
			if (isRedirectCandidate(requestURI)) {
				RedirectURL redirectURL = redirectUrlBuilder.build(request);
				String message = String.format("\n\tINFO: The requested resource %s has been moved. Request will be redirected.\n",
						redirectURL.getUrl().getFile());
				System.out.println(message);
				response.sendRedirect(getRedirect(redirectURL));
			}
		} catch (IOException | RedirectException ex) {
			// TODO how should this be handled?
			ex.printStackTrace();
		}
		return true;
	}

	/**
	 * @param uriString the requested URI to analyse.
	 * @return true if the request URI starts with '<i>/ons</i>', return false otherwise.
	 */
	private boolean isRedirectCandidate(String uriString) {
		return uriString.toLowerCase().startsWith(ONS_URL_PREFIX);
	}

	/**
	 * Gets the redirect mapping for the specified {@link RedirectURL}.<br/><br/>If {@link RedirectURL#getCategory()} equals
	 * {@link com.github.onsdigital.babbage.url.redirect.RedirectCategory#TAXONOMY_REDIRECT} and no '<i>nscl</i>' request
	 * parameter exists returns a redirect to the ONS home page. If the '<i>nscl</i>' request
	 * parameter exists then an attempt is made to find a mapping for the requested resource. If no mapping is found the request
	 * is redirected to the National Archives site.<br/><br/>
	 * <p>
	 * If the RedirectCategory equals {@link com.github.onsdigital.babbage.url.redirect.RedirectCategory#DATA_EXPLORER_REDIRECT}
	 * the requested is redirected to the Data Explorer site using the requested resource from the original request.
	 * <p>
	 * If the RedirectCategory equals {@link com.github.onsdigital.babbage.url.redirect.RedirectCategory#GENERAL_REDIRECT}
	 * an attempt is made to find the redirect mapping. If no mapping is found then returns a redirect to the national archive
	 * site for the requested url.
	 *
	 * @param url the {@link RedirectURL} to find the redirect value for.
	 * @return the redirect value to use.
	 * @throws RedirectException problem getting the redirect value.
	 */
	private String getRedirect(RedirectURL url) throws RedirectException {
		switch (url.getCategory()) {
			case TAXONOMY_REDIRECT:
				return getTaxonomyRedirect(url);
			case DATA_EXPLORER_REDIRECT:
				return urlRedirectService.convertToDataExplorerFormat(url);
			default:
				// Generic Redirects
				return urlRedirectService.convertToNationalArchiveFormat(url);
		}
	}

	private String getTaxonomyRedirect(RedirectURL url) throws RedirectException {
		String redirect = null;
		if (!url.containsParameter()) {
			redirect = "/";
		} else if ((redirect = urlRedirectService.findRedirect(url)) == null) {
			System.out.println("INFO: Redirect URL encountered but no mapping found. Redirecting to requested content on" +
					"national archive site.");
			redirect = urlRedirectService.convertToNationalArchiveFormat(url);
		}
		return redirect;
	}
}
