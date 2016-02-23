package com.github.onsdigital.babbage.url.shortcut;

import com.github.onsdigital.babbage.url.redirect.RedirectException;
import com.github.onsdigital.babbage.url.redirect.UrlRedirectPropertiesService;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Object encapsulating a shortcut URL.
 */
public class ShortcutUrl {

	private static UrlRedirectPropertiesService propertiesService = new UrlRedirectPropertiesService();

	private String shortcut;
	private String redirect;
	private Optional<String> hostKey = Optional.empty();

	public ShortcutUrl(String shortcut, String redirect) {
		this(shortcut, redirect, null);
	}

	public ShortcutUrl(String shortcut, String redirect, String hostKey) {
		this.shortcut = shortcut;
		this.redirect = redirect;
		this.hostKey = StringUtils.isEmpty(hostKey) ? Optional.empty() : Optional.ofNullable(hostKey);
	}

	public String getShortcut() {
		return shortcut;
	}

	public String getRedirect() throws RedirectException {
		StringBuilder url = new StringBuilder();
		if (hostKey.isPresent()) {
			String host = propertiesService.getProperty(hostKey.get());
			url.append(host).append(redirect);
		} else {
			url.append(redirect);
		}
		return url.toString();
	}
}
