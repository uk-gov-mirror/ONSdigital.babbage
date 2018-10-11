package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultNumberIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;

public class MapRenderer implements Loggable {

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
        maxConnections = defaultNumberIfBlank(getNumberValue(MAP_RENDERER_MAX_CONNECTIONS_KEY), 10);
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
    public void logConfiguration() {
        LogBuilder.Log()
                .parameter(MAP_RENDERER_HOST_KEY, host)
                .parameter(MAP_RENDERER_SVG_PATH_KEY, svgPath)
                .parameter(MAP_RENDERER_PNG_PATH_KEY, pngPath)
                .parameter(MAP_RENDERER_MAX_CONNECTIONS_KEY, maxConnections)
                .info("map renderer configuration");
    }
}
