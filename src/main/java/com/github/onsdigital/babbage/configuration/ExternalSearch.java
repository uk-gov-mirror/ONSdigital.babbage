package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultNumberIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;

public class ExternalSearch implements Loggable {

    private static ExternalSearch INSTANCE;

    private static final String EXTERNAL_SEARCH_HOST_KEY = "EXTERNAL_SEARCH_HOST";
    private static final String EXTERNAL_SEARCH_PORT_KEY = "EXTERNAL_SEARCH_PORT";
    private static final String EXTERNAL_SEARCH_ENABLED_KEY = "EXTERNAL_SEARCH_ENABLED";
    private static final String SEARCH_NUM_EXECUTORS_KEY = "SEARCH_NUM_EXECUTORS";

    static ExternalSearch getInstance() {
        if (INSTANCE == null) {
            synchronized (ExternalSearch.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExternalSearch();
                }
            }
        }
        return INSTANCE;
    }

    private final String host;
    private final int port;
    private final boolean enabled;
    private final int executorCount;
    private final String address;

    private ExternalSearch() {
        host = getValueOrDefault(EXTERNAL_SEARCH_HOST_KEY, "localhost");
        port = defaultNumberIfBlank(getNumberValue(EXTERNAL_SEARCH_PORT_KEY), 5000);
        enabled = Boolean.parseBoolean(getValue(EXTERNAL_SEARCH_ENABLED_KEY));
        executorCount = defaultNumberIfBlank(getNumberValue(SEARCH_NUM_EXECUTORS_KEY), 8);
        address = String.format("%s:%d", host, port);
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int executorCount() {
        return executorCount;
    }

    public String address() {
        return address;
    }

    @Override
    public void logConfiguration() {
        LogBuilder.Log()
                .parameter(EXTERNAL_SEARCH_HOST_KEY, host)
                .parameter(EXTERNAL_SEARCH_PORT_KEY, port)
                .parameter(EXTERNAL_SEARCH_ENABLED_KEY, enabled)
                .parameter(SEARCH_NUM_EXECUTORS_KEY, executorCount)
                .info("external search configuration");
    }
}
