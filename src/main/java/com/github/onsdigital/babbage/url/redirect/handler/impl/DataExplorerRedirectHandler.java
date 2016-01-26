package com.github.onsdigital.babbage.url.redirect.handler.impl;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * Redirect Handler implementation that dealS with {@link RedirectCategory#DATA_EXPLORER_REDIRECT} classified redirects.
 */
public final class DataExplorerRedirectHandler extends RedirectHandler {

	@Override
	public void handle(RedirectURL url, HttpServletResponse response) throws RedirectException {
		validate(url, response);
		sendRedirect(response, urlRedirectService.convertToDataExplorerFormat(url));
	}

	@Override
	protected RedirectCategory getRequiredCategory() {
		return RedirectCategory.DATA_EXPLORER_REDIRECT;
	}
}
