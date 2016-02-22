package com.github.onsdigital.babbage.url.shortcuts;

import com.github.onsdigital.babbage.url.AbstractCSVFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * Service provides a mapping of the URLs that have shortcuts.
 */
public class ShortcutUrlService extends AbstractCSVFactory {

	private static final String RESOURCE_LOCATION =  "/url-shortcuts/shortcut-url-mapping.csv";
	private static final ShortcutUrlService instance = new ShortcutUrlService();

	private Optional<Map<String, String>> shortcutMap = Optional.empty();

	/**
	 * @return singleton instance of the ShortcutUrlService.
	 */
	public static ShortcutUrlService getInstance() {
		return instance;
	}

	private ShortcutUrlService() {
		// hide constructor.
	}

	public Map<String, String> shortcuts() throws IOException {
		if (!shortcutMap.isPresent()) {
			shortcutMap = Optional.of(createInstance(RESOURCE_LOCATION)
					.readAll()
					.stream()
					.collect(toMap(list -> list[0], list -> list[1])));
		}
		return shortcutMap.get();
	}
}
