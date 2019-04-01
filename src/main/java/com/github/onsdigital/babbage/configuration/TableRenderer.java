package com.github.onsdigital.babbage.configuration;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class TableRenderer implements Loggable {

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
    public void logConfiguration() {
        info().data(TABLE_RENDERER_HOST_KEY, host)
                .data(TABLE_RENDERER_HTML_PATH_KEY, htmlPath)
                .data(MAX_RENDERER_CONNECTIONS_KEY, maxConnections)
                .log("table renderer configuration");
    }
}
