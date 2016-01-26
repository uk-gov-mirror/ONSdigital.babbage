package com.github.onsdigital.babbage.url.redirect.handler;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.INVALID_REDIRECT_CATEGORY;
import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REDIRECT_IO_ERROR;
import static java.util.Objects.requireNonNull;

/**
 * Provides common functionality for all redirect handlers.
 */
public abstract class RedirectHandler {

	protected final UrlRedirectService urlRedirectService = UrlRedirectService.getInstance();

	/**
	 * Redirects to the appropriate place - determined by {@link RedirectURL#category}.
	 *
	 * @param url      the {@link RedirectURL} to redirect.
	 * @param response the {@link HttpServletResponse} to use to perform the redirect.
	 * @throws RedirectException thrown if there are any problems while attempting to redirect.
	 */
	public abstract void handle(RedirectURL url, HttpServletResponse response) throws RedirectException;

	protected abstract RedirectCategory getRequiredCategory();

	protected void sendRedirect(HttpServletResponse response, String redirect) throws RedirectException {
		try {
			response.sendRedirect(redirect);
		} catch (IOException io) {
			throw new RedirectException(io, REDIRECT_IO_ERROR);
		}
	}

	protected void validate(RedirectURL url, HttpServletResponse response) throws RedirectException {
		requireNonNull(url, "url is required and cannot be null");
		requireNonNull(response, "response is required and cannot be null");
		requireNonNull(url.getCategory(), "url.category cannot be null");
		if (!getRequiredCategory().equals(url.getCategory())) {
			throw new RedirectException(INVALID_REDIRECT_CATEGORY,
					new Object[]{getRequiredCategory().name(), url.getCategory().name()});
		}
	}
}
