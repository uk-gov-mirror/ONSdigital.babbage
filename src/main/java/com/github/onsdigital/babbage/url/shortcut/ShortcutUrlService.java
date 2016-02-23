package com.github.onsdigital.babbage.url.shortcut;

import com.github.onsdigital.babbage.url.AbstractCSVFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service provides a mapping of the URLs that have shortcuts.
 */
public class ShortcutUrlService extends AbstractCSVFactory {

	private static final String RESOURCE_LOCATION =  "/url-shortcuts/shortcut-url-mapping.csv";
	private static final ShortcutUrlService instance = new ShortcutUrlService();

	private Optional<List<ShortcutUrl>> shortcuts = Optional.empty();

	/**
	 * @return singleton instance of the ShortcutUrlService.
	 */
	public static ShortcutUrlService getInstance() {
		return instance;
	}

	private ShortcutUrlService() {
		// hide constructor.
	}

	public List<ShortcutUrl> shortcuts() throws IOException {
		if (!shortcuts.isPresent()) {
			shortcuts = Optional.of(createInstance(RESOURCE_LOCATION)
					.readAll()
					.stream()
					.map(list -> new ShortcutUrl(list[0], list[1], list[2]))
					.collect(Collectors.toList()));
		}
		return shortcuts.get();
	}
}
