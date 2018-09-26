package com.github.onsdigital.babbage.configuration;

public class AppConfiguration {

    private static AppConfiguration INSTANCE;

    private final ElasticSearch elasticSearch;
    private final ContentAPI contentAPI;
    private final Babbage babbage;
    private final Handlebars handlebars;
    private final TableRenderer tableRenderer;

    public static void loadConfiguration() {
        if (INSTANCE == null) {
            synchronized (AppConfiguration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppConfiguration();
                }
            }
        }
    }

    private AppConfiguration() {
        elasticSearch = ElasticSearch.getInstance();
        contentAPI = ContentAPI.getInstance();
        babbage = Babbage.getInstance();
        handlebars = Handlebars.getInstance();
        tableRenderer = TableRenderer.getInstance();

        logApplicationConfigs(elasticSearch, contentAPI, babbage, handlebars, tableRenderer);
    }

    private void logApplicationConfigs(Loggable... configs) {
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

    public static AppConfiguration appConfig() {
        return INSTANCE;
    }
}
