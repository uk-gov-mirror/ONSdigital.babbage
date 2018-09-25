package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;
import org.apache.commons.lang3.StringUtils;

import static com.github.onsdigital.babbage.configuration.Utils.defaultNumberIfBlank;
import static com.github.onsdigital.babbage.configuration.Utils.getNumberValue;
import static com.github.onsdigital.babbage.configuration.Utils.getValue;

public class Babbage implements Loggable {

    private static Babbage INSTANCE;

    static Babbage getInstance() {
        if (INSTANCE == null) {
            synchronized (Babbage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Babbage();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * cache timeout in seconds, to be set as HTTP max age header
     */
    private final int defaultCacheTime;

    /**
     * If content that should be published is more than an hour due delete publish date to get it caching again
     **/
    private final int publishCacheTimeout;

    /**
     * search results max age header in seconds
     **/
    private final long searchResponseCacheTime;

    private final int maxVisiblePaginatorLink;
    private final int resultsPerPage;
    private final int maxResultsPerPage;
    private final boolean cacheEnabled;
    private final boolean isDevEnv;
    private final boolean isPublishing;
    private final String redirectSecret;
    private final String phantomjsPath;
    private final int maxHighchartsServerConnections;
    private final String exportSeverUrl;
    private final String ghostscriptPath;
    private final String mathjaxExportServer;

    private Babbage() {
        maxVisiblePaginatorLink = 5;
        resultsPerPage = 10;
        maxResultsPerPage = 250;
        defaultCacheTime = 15 * 60;
        publishCacheTimeout = 60 * 60;
        searchResponseCacheTime = 5;

        // TODO - DISCLAIMER I didn't write thisit was copied from an existing class.
        // TODO Fix this to use true or false instead of this overly complicated & stupid approach.
        cacheEnabled = "Y".equals(StringUtils.defaultIfBlank(getValue("ENABLE_CACHE"), "N"));

        // sigh...
        isDevEnv = "Y".equals(StringUtils.defaultIfBlank(getValue("DEV_ENVIRONMENT"), "N"));

        // sigh...
        isPublishing = "Y".equals(StringUtils.defaultIfBlank(getValue("IS_PUBLISHING"), "N"));

        redirectSecret = StringUtils.defaultIfBlank(getValue("REDIRECT_SECRET"), "secret");

        phantomjsPath = StringUtils.defaultIfBlank(getValue("PHANTOMJS_PATH"), "/usr/local/bin/phantomjs");

        maxHighchartsServerConnections = defaultNumberIfBlank(getNumberValue("HIGHCHARTS_EXPORT_MAX_CONNECTION"), 50);

        exportSeverUrl = StringUtils.defaultIfBlank(getValue("HIGHCHARTS_EXPORT_SERVER"), "http://localhost:9999/");

        ghostscriptPath = StringUtils.defaultIfBlank(getValue("GHOSTSCRIPT_PATH"), "/usr/local/bin/gs");

        mathjaxExportServer = getValue("MATHJAX_EXPORT_SERVER");
    }

    public int getDefaultContentCacheTime() {
        return defaultCacheTime;
    }

    public long getSearchResponseCacheTime() {
        return searchResponseCacheTime;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public int getMaxVisiblePaginatorLink() {
        return maxVisiblePaginatorLink;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public int getMaxResultsPerPage() {
        return maxResultsPerPage;
    }

    public String getRedirectSecret() {
        return redirectSecret;
    }

    public boolean isDevEnvironment() {
        return isDevEnv;
    }

    public boolean isPublishing() {
        return isPublishing;
    }

    public int getPublishCacheTimeout() {
        return publishCacheTimeout;
    }

    public String getPhantomjsPath() {
        return phantomjsPath;
    }

    public int getDefaultCacheTime() {
        return defaultCacheTime;
    }

    public boolean isDevEnv() {
        return isDevEnv;
    }

    public int getMaxHighchartsServerConnections() {
        return maxHighchartsServerConnections;
    }

    public String getExportSeverUrl() {
        return exportSeverUrl;
    }

    public String getGhostscriptPath() {
        return ghostscriptPath;
    }

    public String getMathjaxExportServer() {
        return mathjaxExportServer;
    }

    public void logConfiguration() {
        LogBuilder.Log()
                .parameter("maxVisiblePaginatorLink", maxVisiblePaginatorLink)
                .parameter("resultsPerPage", resultsPerPage)
                .parameter("maxResultsPerPage", maxResultsPerPage)
                .parameter("defaultCacheTime", defaultCacheTime)
                .parameter("publishCacheTimeout", publishCacheTimeout)
                .parameter("searchResponseCacheTime", searchResponseCacheTime)
                .parameter("cacheEnabled", cacheEnabled)
                .parameter("isDevEnv", isDevEnv)
                .parameter("isPublishing", isPublishing)
                .parameter("phantomjsPath", phantomjsPath)
                .parameter("maxHighchartsServerConnections", maxHighchartsServerConnections)
                .parameter("exportSeverUrl", exportSeverUrl)
                .parameter("ghostscriptPath", ghostscriptPath)
                .parameter("mathjaxExportServer", mathjaxExportServer)
                .info("babbage general configuration");
    }
}
