package com.github.onsdigital.babbage.configuration;

import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class MapRenderer implements AppConfig {

    private static MapRenderer INSTANCE;

    private static final String MAP_RENDERER_HOST_KEY = "MAP_RENDERER_HOST";
    private static final String MAP_RENDERER_SVG_PATH_KEY = "MAP_RENDERER_SVG_PATH";
    private static final String MAP_RENDERER_PNG_PATH_KEY = "MAP_RENDERER_PNG_PATH";
    private static final String MAP_RENDERER_MAX_CONNECTIONS_KEY = "MAP_RENDERER_MAX_CONNECTIONS";

    private final String host;
    private final String svgPath;
    private final String pngPath;
    private final int maxConnections;

    static MapRenderer getInstance() {
        if (INSTANCE == null) {
            synchronized (MapRenderer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MapRenderer();
                }
            }
        }
        return INSTANCE;
    }

    private MapRenderer() {
        host = getValueOrDefault(MAP_RENDERER_HOST_KEY, "http://localhost:23500");
        svgPath = getValueOrDefault(MAP_RENDERER_SVG_PATH_KEY, "/render/svg");
        pngPath = getValueOrDefault(MAP_RENDERER_PNG_PATH_KEY, "/render/png");
        maxConnections = defaultIfBlank(getNumberValue(MAP_RENDERER_MAX_CONNECTIONS_KEY), 10);
    }

    public String host() {
        return host;
    }

    public String svgPath() {
        return svgPath;
    }

    public String pngPath() {
        return pngPath;
    }

    public int maxConnections() {
        return maxConnections;
    }

    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(MAP_RENDERER_HOST_KEY, host);
        config.put(MAP_RENDERER_SVG_PATH_KEY, svgPath);
        config.put(MAP_RENDERER_PNG_PATH_KEY, pngPath);
        config.put(MAP_RENDERER_MAX_CONNECTIONS_KEY, maxConnections);
        return config;
    }
}
