package com.github.onsdigital.babbage.configuration;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class Configuration {

    public static class SEARCH_SERVICE {
        private static final String HOST = defaultIfBlank(getValue("EXTERNAL_SEARCH_HOST"), "localhost");
        private static final int PORT = defaultNumberIfBlank(getNumberValue("EXTERNAL_SEARCH_PORT"), 5000);
        public static final boolean EXTERNAL_SEARCH_ENABLED = Boolean.parseBoolean(getValue("ENABLE_SEARCH_SERVICE"));
        public static final int SEARCH_NUM_EXECUTORS = defaultNumberIfBlank(getNumberValue("SEARCH_NUM_EXECUTORS"), 8);

        public static final String getExternalSearchAddress() {
            return String.format("%s:%d", HOST, PORT);
        }
    }

    /**
     * Server side table rendering configuration.
     */
    public static class TABLE_RENDERER {
        private static final String HOST = defaultIfBlank(getValue("TABLE_RENDERER_HOST"), "http://localhost:23300");
        private static final String HTML_PATH = defaultIfBlank(getValue("TABLE_RENDERER_HTML_PATH"), "/render/html");
        private static final int MAX_RENDERER_CONNECTIONS = defaultNumberIfBlank(getNumberValue("TABLE_RENDERER_MAX_CONNECTIONS"), 10);

        /**
         * @return the hostname of the table renderer).
         */
        public static String getHost() {
            return HOST;
        }

        /**
         * @return the path to invoke when rendering an html table).
         */
        public static String getHtmlPath() {
            return HTML_PATH;
        }

        public static int getMaxServerConnection() {
            return MAX_RENDERER_CONNECTIONS;
        }
    }

    /**
     * Server side map rendering configuration.
     */
    public static class MAP_RENDERER {
        private static final String HOST = defaultIfBlank(getValue("MAP_RENDERER_HOST"), "http://localhost:23500");
        private static final String SVG_PATH = defaultIfBlank(getValue("MAP_RENDERER_SVG_PATH"), "/render/svg");
        private static final String PNG_PATH = defaultIfBlank(getValue("MAP_RENDERER_PNG_PATH"), "/render/png");
        private static final int MAX_RENDERER_CONNECTIONS = defaultNumberIfBlank(getNumberValue("MAP_RENDERER_MAX_CONNECTIONS"), 10);

        /**
         * @return the hostname of the table renderer.
         */
        public static String getHost() {
            return HOST;
        }

        /**
         * @return the path to invoke when rendering an svg map.
         */
        public static String getSvgPath() {
            return SVG_PATH;
        }

        /**
         * @return the path to invoke when rendering a png map.
         */
        public static String getPngPath() {
            return PNG_PATH;
        }

        public static int getMaxServerConnection() {
            return MAX_RENDERER_CONNECTIONS;
        }
    }

    /**
     * Gets a configured value for the given key from either the system
     * properties or an environment variable.
     * <p>
     * Copied from {@link com.github.davidcarboni.restolino.Configuration}.
     *
     * @param key The title of the configuration value.
     * @return The system property corresponding to the given key (e.g.
     * -Dkey=value). If that is blank, the environment variable
     * corresponding to the given key (e.g. EXPORT key=value). If that
     * is blank, {@link StringUtils#EMPTY}.
     */
    private static String getValue(String key) {
        return StringUtils.defaultIfBlank(System.getProperty(key), System.getenv(key));
    }

    private static Integer getNumberValue(String key) {
        String value = getValue(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value.trim());
    }


    private static Integer defaultNumberIfBlank(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

}
