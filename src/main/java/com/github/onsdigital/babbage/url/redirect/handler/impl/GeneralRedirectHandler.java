package com.github.onsdigital.babbage.url.redirect.handler.impl;

import com.github.onsdigital.babbage.url.redirect.RedirectCategory;
import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.RedirectURL;
import com.github.onsdigital.babbage.url.redirect.handler.RedirectHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * Redirect Handler implementation that dealS with {@link RedirectCategory#GENERAL_REDIRECT} classified redirects.
 */
public final class GeneralRedirectHandler extends RedirectHandler {

	@Override
	public void handle(RedirectURL url, HttpServletResponse response) throws RedirectException {
		validate(url, response);
		String redirect;
		if (StringUtils.isEmpty(redirect = urlRedirectService.findRedirect(url))) {
			redirect = urlRedirectService.convertToNationalArchiveFormat(url);
		}
		sendRedirect(response, redirect);
	}

	@Override
	protected RedirectCategory getRequiredCategory() {
		return RedirectCategory.GENERAL_REDIRECT;
	}
}
