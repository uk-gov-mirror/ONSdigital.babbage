package com.github.onsdigital.babbage.configuration;

import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

/**
 * ApplicationConfiguration is responsible for loading the babbage configuration classes and providing a single
 * access point to the different configurations. ApplicationConfiguration is a lazy loaded singleton - it is strongly
 * recommended that {@link ApplicationConfiguration#init()} is called from
 * {@link com.github.onsdigital.babbage.Init} on startup, however {@link ApplicationConfiguration#appConfig()} will
 * instantiate the config if it has not already been loaded.
 */
public class ApplicationConfiguration {

    private static ApplicationConfiguration INSTANCE;

    private final ElasticSearch elasticSearch;
    private final ContentAPI contentAPI;
    private final Babbage babbage;
    private final Handlebars handlebars;
    private final TableRenderer tableRenderer;
    private final ExternalSearch externalSearch;
    private final MapRenderer mapRenderer;

    /**
     * Load the application configuration
     */
    public static ApplicationConfiguration init() {
        return getInstance();
    }

    private static ApplicationConfiguration getInstance() {
        if (INSTANCE == null) {
            synchronized (ApplicationConfiguration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApplicationConfiguration();
                }
            }
        }
        return INSTANCE;
    }

    public static ApplicationConfiguration appConfig() {
        return getInstance();
    }

    private ApplicationConfiguration() {
        elasticSearch = ElasticSearch.getInstance();
        contentAPI = ContentAPI.getInstance();
        babbage = Babbage.getInstance();
        handlebars = Handlebars.getInstance();
        tableRenderer = TableRenderer.getInstance();
        externalSearch = ExternalSearch.getInstance();
        mapRenderer = MapRenderer.getInstance();

        logApplicationConfigs(elasticSearch, contentAPI, babbage,
                handlebars, tableRenderer, externalSearch, mapRenderer);
    }

    private void logApplicationConfigs(Loggable... configs) {
        logEvent().info("loading babbage startup configurations");
        for (Loggable l : configs) {
            l.logConfiguration();
        }
    }

    public ElasticSearch elasticSearch() {
        return elasticSearch;
    }

    public ContentAPI contentAPI() {
        return contentAPI;
    }

    public Babbage babbage() {
        return babbage;
    }

    public Handlebars handlebars() {
        return handlebars;
    }

    public TableRenderer tableRenderer() {
        return tableRenderer;
    }

    public ExternalSearch externalSearch() {
        return externalSearch;
    }

    public MapRenderer mapRenderer() {
        return mapRenderer;
    }

}
