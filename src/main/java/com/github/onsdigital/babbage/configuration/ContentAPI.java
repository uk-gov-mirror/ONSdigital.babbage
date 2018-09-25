package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;
import org.apache.commons.lang3.StringUtils;

import static com.github.onsdigital.babbage.configuration.Utils.defaultNumberIfBlank;
import static com.github.onsdigital.babbage.configuration.Utils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.Utils.getValue;

public class ContentAPI {

    // Configuration environment var keys
    private static final String CONTENT_API_HOST_KEY = "CONTENT_SERVICE_URL";
    private static final String MAX_CONNECTIONS_KEY = "CONTENT_SERVICE_MAX_CONNECTION";

    private static ContentAPI INSTANCE;

    private static final String DEFAULT_CONTENT_API_HOST = "http://localhost:8082";
    private static final String DEFAULT_CONTENT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATA_ENDPOINT = "/data";
    private static final String TAXONOMY_ENDPOINT = "/taxonomy";
    private static final String PARENTS_ENDPOINT = "/parents";
    private static final String RESOURCE_ENDPOINT = "/resource";
    private static final String FILE_SIZE_ENDPOINT = "/filesize";
    private static final String REINDEX_ENDPOINT = "/reindex";
    private static final String GENERATOR_ENDPOINT = "/generator";
    private static final String EXPORT_ENDPOINT = "/export";

    private final String contentAPIHost;
    private final int maxConnections;

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
    }

    public String getServerUrl() {
        return contentAPIHost;
    }

    public String getDataEndpoint() {
        return DATA_ENDPOINT;
    }

    public String getResourceEndpoint() {
        return RESOURCE_ENDPOINT;
    }

    public String getFileSizeEndpoint() {
        return FILE_SIZE_ENDPOINT;
    }

    public String getTaxonomyEndpoint() {
        return TAXONOMY_ENDPOINT;
    }

    public String getParentsEndpoint() {
        return PARENTS_ENDPOINT;
    }

    public String getReindexEndpoint() {
        return REINDEX_ENDPOINT;
    }

    public String getGeneratorEndpoint() {
        return GENERATOR_ENDPOINT;
    }

    public String getExportEndpoint() {
        return EXPORT_ENDPOINT;
    }

    public int getMaxContentServiceConnection() {
        return maxConnections;
    }

    public String getDefaultContentDatePattern() {
        return DEFAULT_CONTENT_DATE_PATTERN;
    }

    public void logConfiguration() {
        LogBuilder.Log()
                .parameter(CONTENT_API_HOST_KEY, contentAPIHost)
                .parameter(MAX_CONNECTIONS_KEY, maxConnections)
                .info("content API configuration");
    }
}
