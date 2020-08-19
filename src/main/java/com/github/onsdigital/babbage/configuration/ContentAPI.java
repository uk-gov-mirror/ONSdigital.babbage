package com.github.onsdigital.babbage.configuration;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;

public class ContentAPI implements AppConfig {

    // Configuration environment var keys
    private static final String API_ROUTER_HOST_KEY = "API_ROUTER_URL";
    private static final String MAX_CONNECTIONS_KEY = "CONTENT_SERVICE_MAX_CONNECTION";

    private static ContentAPI INSTANCE;

    private static final String DEFAULT_API_ROUTER_HOST = "http://localhost:23200/v1";
    private static final String DEFAULT_CONTENT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final String APIRouterHost;
    private final int maxConnections;
    private SimpleDateFormat defaultSimpleDataFormat;

    static ContentAPI getInstance() {
        if (INSTANCE == null) {
            synchronized (ContentAPI.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ContentAPI();
                }
            }
        }
        return INSTANCE;
    }

    private ContentAPI() {
        APIRouterHost = StringUtils.removeEnd(getValueOrDefault(API_ROUTER_HOST_KEY, DEFAULT_API_ROUTER_HOST), "/");
        maxConnections = defaultIfBlank(getNumberValue(MAX_CONNECTIONS_KEY), 50);
        defaultSimpleDataFormat = new SimpleDateFormat(DEFAULT_CONTENT_DATE_PATTERN);
    }

    public String serverURL() {
        return APIRouterHost;
    }

    public int maxConnections() {
        return maxConnections;
    }

    public String defaultContentDatePattern() {
        return DEFAULT_CONTENT_DATE_PATTERN;
    }

    public SimpleDateFormat defaultContentDateFormat() {
        return defaultSimpleDataFormat;
    }

    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(API_ROUTER_HOST_KEY, APIRouterHost);
        config.put(MAX_CONNECTIONS_KEY, maxConnections);
        config.put("defaultContentDatePattern", DEFAULT_CONTENT_DATE_PATTERN);
        return config;
    }
}
