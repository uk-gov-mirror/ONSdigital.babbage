package com.github.onsdigital.babbage.configuration;

public class AppConfiguration {

    private static AppConfiguration INSTANCE;

    private final ElasticSearch elasticSearch;
    private final ContentAPI contentAPI;

    private AppConfiguration() {
        elasticSearch = ElasticSearch.getInstance();
        elasticSearch.logConfiguration();

        contentAPI = ContentAPI.getInstance();
        contentAPI.logConfiguration();
    }

    public static void loadConfiguration() {
        if (INSTANCE == null) {
            synchronized (AppConfiguration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppConfiguration();
                }
            }
        }
    }

    public ElasticSearch elasticSearch() {
        return elasticSearch;
    }

    public static AppConfiguration appConfig() {
        return INSTANCE;
    }
}
