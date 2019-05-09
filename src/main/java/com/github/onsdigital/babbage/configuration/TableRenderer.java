package com.github.onsdigital.babbage.configuration;

import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;

public class TableRenderer implements AppConfig {

    private static final String TABLE_RENDERER_HOST_KEY = "TABLE_RENDERER_HOST";
    private static final String TABLE_RENDERER_HTML_PATH_KEY = "TABLE_RENDERER_HTML_PATH";
    private static final String MAX_RENDERER_CONNECTIONS_KEY = "MAX_RENDERER_CONNECTIONS";

    private static TableRenderer INSTANCE;

    private final String host;
    private final String htmlPath;
    private final int maxConnections;

    static TableRenderer getInstance() {
        if (INSTANCE == null) {
            synchronized (TableRenderer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TableRenderer();
                }
            }
        }
        return INSTANCE;
    }

    private TableRenderer() {
        host = getValueOrDefault(TABLE_RENDERER_HOST_KEY, "http://localhost:23300");
        htmlPath = getValueOrDefault(TABLE_RENDERER_HTML_PATH_KEY, "/render/html");
        maxConnections = defaultIfBlank(getNumberValue(MAX_RENDERER_CONNECTIONS_KEY), 10);
    }

    public String host() {
        return host;
    }

    public String htmlPath() {
        return htmlPath;
    }

    public int maxConnections() {
        return maxConnections;
    }

    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(TABLE_RENDERER_HOST_KEY, host);
        config.put(TABLE_RENDERER_HTML_PATH_KEY, htmlPath);
        config.put(MAX_RENDERER_CONNECTIONS_KEY, maxConnections);
        return config;
    }
}
