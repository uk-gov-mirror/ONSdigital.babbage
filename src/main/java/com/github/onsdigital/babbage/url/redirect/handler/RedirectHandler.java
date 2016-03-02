package com.github.onsdigital.babbage.url.redirect.handler;

import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.onsdigital.babbage.url.redirect.RedirectException.ErrorType.REDIRECT_IO_ERROR;
import static java.util.Objects.requireNonNull;

/**
 * Provides common functionality for all redirect handlers.
 */
public abstract class RedirectHandler {

	protected final UrlRedirectService urlRedirectService = UrlRedirectService.getInstance();

	public abstract void handle(HttpServletRequest request, HttpServletResponse response) throws RedirectException;

	protected void sendRedirect(HttpServletResponse response, String redirect) throws RedirectException {
		try {
			response.sendRedirect(redirect);
		} catch (IOException io) {
			throw new RedirectException(io, REDIRECT_IO_ERROR);
		}
	}

	protected void validate(HttpServletRequest request, HttpServletResponse response) throws RedirectException {
		requireNonNull(request, "request is required and cannot be null");
		requireNonNull(response, "response is required and cannot be null");
	}
}
