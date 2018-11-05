package com.github.onsdigital.babbage.configuration;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.defaultIfBlank;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getFloatValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValue;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;
import static com.github.onsdigital.babbage.logging.LogEvent.logEvent;

public class ExternalSearch implements Loggable {

    private static ExternalSearch INSTANCE;

    private static final String DEFAULT_SEARCH_CLIENT = "external";
    private static final String EXTERNAL_SEARCH_HOST_KEY = "EXTERNAL_SEARCH_HOST";
    private static final String EXTERNAL_SEARCH_PORT_KEY = "EXTERNAL_SEARCH_PORT";
    private static final String EXTERNAL_SEARCH_ENABLED_KEY = "EXTERNAL_SEARCH_ENABLED";
    private static final String SEARCH_NUM_EXECUTORS_KEY = "SEARCH_NUM_EXECUTORS";
    private static final String EXTERNAL_SPELLCHECK_ENABLED_KEY = "EXTERNAL_SPELLCHECK_ENABLED";
    private static final String SPELL_CHECK_CONFIDENCE_THRESHOLD_KEY = "SPELL_CHECK_CONFIDENCE_THRESHOLD";

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

    private final String defaultSearchClient;
    private final String host;
    private final int port;
    private final boolean enabled;
    private final int executorCount;
    private final String address;
    private final boolean spellCheckEnabled;
    private final float spellCheckConfidenceThreshold;

    private ExternalSearch() {
        defaultSearchClient = "external";
        host = getValueOrDefault(EXTERNAL_SEARCH_HOST_KEY, "localhost");
        port = EnvVarUtils.defaultIfBlank(getNumberValue(EXTERNAL_SEARCH_PORT_KEY), 5000);
        enabled = Boolean.parseBoolean(getValue(EXTERNAL_SEARCH_ENABLED_KEY));
        executorCount = EnvVarUtils.defaultIfBlank(getNumberValue(SEARCH_NUM_EXECUTORS_KEY), 8);
        address = String.format("%s:%d", host, port);
        spellCheckEnabled = Boolean.parseBoolean(getValue(EXTERNAL_SPELLCHECK_ENABLED_KEY));
        spellCheckConfidenceThreshold = defaultIfBlank(getFloatValue(SPELL_CHECK_CONFIDENCE_THRESHOLD_KEY), 0.0f);
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


    public boolean isSpellCheckEnabled() {
        return spellCheckEnabled;
    }

    public float spellCheckConfidenceThreshold() {
        return spellCheckConfidenceThreshold;
    }

    public String defaultSearchClient() {
        return defaultSearchClient;
    }

    @Override
    public void logConfiguration() {
        logEvent()
                .parameter(EXTERNAL_SEARCH_HOST_KEY, host)
                .parameter(EXTERNAL_SEARCH_PORT_KEY, port)
                .parameter(EXTERNAL_SEARCH_ENABLED_KEY, enabled)
                .parameter(EXTERNAL_SPELLCHECK_ENABLED_KEY, spellCheckEnabled)
                .parameter(SPELL_CHECK_CONFIDENCE_THRESHOLD_KEY, spellCheckConfidenceThreshold)
                .parameter(SEARCH_NUM_EXECUTORS_KEY, executorCount)
                .info("external search configuration");
    }
}
