package com.github.onsdigital.babbage.configuration;

public class AppConfiguration {

    private static AppConfiguration INSTANCE;

    private final ElasticSearch elasticSearch;
    private final ContentAPI contentAPI;
    private final Babbage babbage;

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

        logApplicationConfigs(elasticSearch, contentAPI, babbage);
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

    public static AppConfiguration appConfig() {
        return INSTANCE;
    }

}
