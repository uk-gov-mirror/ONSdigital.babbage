package com.github.onsdigital.babbage.api.endpoint.rss.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

/**
 * Service for obtaining RSS relates properties/configuration.
 */
public class PropertiesService {

    private static final String PROPERTIES_RESOURCE_PATH = "/rss_feed.properties";
    private static Optional<Properties> propertiesOptional = Optional.empty();
    private static final PropertiesService INSTANCE = new PropertiesService();

    public static PropertiesService getInstance() {
        return PropertiesService.INSTANCE;
    }

    private PropertiesService() {
        // hide constructor.
    }

    public String get(String key) {
        if (!propertiesOptional.isPresent()) {
            loadProperties();
        }
        return String.valueOf(propertiesOptional.get().get(key));
    }

    private static void loadProperties() {
        if (propertiesOptional.isPresent()) {
            return;
        }
        try (InputStream in = RssService.class.getResourceAsStream(PROPERTIES_RESOURCE_PATH)) {
            propertiesOptional = propertiesOptional.of(new Properties());
            propertiesOptional.get().load(in);
        } catch (IOException io) {
            error().exception(io).log("unexpected error while attempting to load RSS properties");
        }
    }
}
