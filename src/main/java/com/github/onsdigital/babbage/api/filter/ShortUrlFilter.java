package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.babbage.url.shortcuts.ShortcutUrlService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * URL Shortcut filter: allows shortened url to be redirected to their actual location.
 */
public class ShortUrlFilter implements Filter {

	private static Optional<Map<String, String>> shortcuts = Optional.empty();

	private ShortcutUrlService shortcutUrlService = ShortcutUrlService.getInstance();

	@Override
	public boolean filter(HttpServletRequest req, HttpServletResponse res) {
		try {
			String uri = req.getRequestURI().toLowerCase();
			String shortCut;
			if ((shortCut = shortcuts().get(uri)) != null) {
				res.sendRedirect(shortCut);
				res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				return false;
			}
		} catch (IOException ex) {
			String msg = String.format("Unexpected error while attempting to find a shortcut url redirect for '%s'.",
					req.getRequestURI());
			throw new RuntimeException(msg, ex);
		}
		return true;
	}

	private Map<String, String> shortcuts() throws IOException {
		if (!shortcuts.isPresent()) {
			shortcuts = Optional.of(shortcutUrlService.shortcuts());
		}
		return shortcuts.get();
	}
}
