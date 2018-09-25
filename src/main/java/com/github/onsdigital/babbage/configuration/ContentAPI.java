package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

import static com.github.onsdigital.babbage.configuration.Utils.defaultNumberIfBlank;
import static com.github.onsdigital.babbage.configuration.Utils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.Utils.getValue;

public class ContentAPI implements Loggable {

    // Configuration environment var keys
    private static final String CONTENT_API_HOST_KEY = "CONTENT_SERVICE_URL";
    private static final String MAX_CONNECTIONS_KEY = "CONTENT_SERVICE_MAX_CONNECTION";

    private static ContentAPI INSTANCE;

    private static final String DEFAULT_CONTENT_API_HOST = "http://localhost:8082";
    private static final String DEFAULT_CONTENT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final String contentAPIHost;
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
        contentAPIHost = StringUtils.removeEnd(StringUtils.defaultIfBlank(getValue(CONTENT_API_HOST_KEY), DEFAULT_CONTENT_API_HOST), "/");
        maxConnections = defaultNumberIfBlank(getNumberValue(MAX_CONNECTIONS_KEY), 50);
        defaultSimpleDataFormat = new SimpleDateFormat(DEFAULT_CONTENT_DATE_PATTERN);
    }

    public String serverURL() {
        return contentAPIHost;
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

    public void logConfiguration() {
        LogBuilder.Log()
                .parameter(CONTENT_API_HOST_KEY, contentAPIHost)
                .parameter(MAX_CONNECTIONS_KEY, maxConnections)
                .parameter("defaultContentDatePattern", DEFAULT_CONTENT_DATE_PATTERN)
                .info("content API configuration");
    }
}
