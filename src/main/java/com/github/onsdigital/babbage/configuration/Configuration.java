package com.github.onsdigital.babbage.configuration;

import com.github.davidcarboni.cryptolite.*;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;

public class Configuration {

    /*General Babbage app settings*/
    public static class GENERAL {
        private static final int MAX_VISIBLE_PAGINATOR_LINK = 10;
        private static final int GLOBAL_CACHE_TIMEOUT = 5;
        private static final int GLOBAL_REQUEST_CACHE_SIZE = 1000;


        public static int getMaxVisiblePaginatorLink() {
            return MAX_VISIBLE_PAGINATOR_LINK;
        }

        public static int getGlobalCacheTimeout() {
            return GLOBAL_CACHE_TIMEOUT;
        }

        public static int getGlobalRequestCacheSize() {
            return Integer.parseInt(StringUtils.defaultIfBlank(getValue("GLOBAL_CACHE_SIZE"), String.valueOf(GLOBAL_REQUEST_CACHE_SIZE)));
        }

        public static boolean isCacheEnabled() {
            String babbage_env = StringUtils.defaultIfBlank(getValue("ENABLE_CACHE"), "");
            return "Y".equals(babbage_env);
        }

    }

    /*External content server configuration*/
    public static class CONTENT_SERVICE {
        private static final String SERVER_URL =  StringUtils.removeEnd(StringUtils.defaultIfBlank(getValue("CONTENT_SERVICE_URL"), "http://localhost:8082"), "/");
        private static final String DATA_ENDPOINT = "/data";
        private static final String CHILDREN_ENDPOINT = "/children";
        private static final String PARENTS_ENDPOINT = "/parents";
        private static final String RESOURCE_ENDPOINT = "/resource";
        private static final String FILE_SIZE_ENDPOINT = "/filesize";
        private static final String SEARCH_ENDPOINT = "/search";
        private static final String LIST_ENDPOINT = "/list";
        private static final int MAX_CONTENT_SERVICE_CONNECTION = defaultNumberIfBlank(getNumberValue("CONTENT_SERVICE_MAX_CONNECTION"), 50);
        private static final String DEFAULT_CONTENT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        public static String getServerUrl() {
            return SERVER_URL;
        }

        public static String getDataEndpoint() {
            return DATA_ENDPOINT;
        }

        public static String getResourceEndpoint() { return RESOURCE_ENDPOINT; }

        public static String getFileSizeEndpoint() { return FILE_SIZE_ENDPOINT; }

        public static String getChildrenEndpoint() { return CHILDREN_ENDPOINT; }

        public static String getParentsEndpoint() {return PARENTS_ENDPOINT; }

        public static String getSearchEndpoint() { return SEARCH_ENDPOINT; }

        public static String getListEndpoint() { return LIST_ENDPOINT; }

        public static int getMaxContentServiceConnection() {
            return MAX_CONTENT_SERVICE_CONNECTION;
        }

        public static String getDefaultContentDatePattern() {
            return DEFAULT_CONTENT_DATE_PATTERN;
        }

        //TODO: get rid of content path, should only be using content service
        private static final String DEFAULT_CONTENT_DIRECTORY = "target/content";

        public static String getContentPath() {
            return StringUtils.defaultIfBlank(getValue("CONTENT_DIR"), DEFAULT_CONTENT_DIRECTORY);
        }



    }


    //TODO: Delete zebedee configuration , will use content service
    public static class ZEBEDEE {
        private static final String DEFAULT_ZEBEDEE_URL = "http://localhost:8082";

        public static String getZebedeeUrl() {
            return StringUtils.defaultIfBlank(getValue("ZEBEDEE_URL"), DEFAULT_ZEBEDEE_URL);
        }
    }


    public static class ELASTIC_SEARCH {
        private static final String ELASTIC_SEARCH_URL = StringUtils.defaultIfBlank(getValue("ELASTIC_SEARCH_URL"), "http://localhost:8090");

        public static String getELASTIC_SEARCH_URL() {
            return ELASTIC_SEARCH_URL;
        }
    }


    /*Handlebars configuration*/
    public static class HANDLEBARS {
        private static final String DEFAULT_HANDLEBARS_DATE_PATTERN = "d MMMM yyyy";
        private static final String TEMPLATES_DIR = StringUtils.defaultIfBlank(getValue("TEMPLATES_DIR"), "src/main/web/templates/handlebars");
        private static final String TEMPLATES_SUFFIX = StringUtils.defaultIfBlank(getValue("TEMPLATES_SUFFIX"), ".handlebars");
        private static final String MAIN_CONTENT_TEMPLATE_NAME =  "main";

        public static String getHandlebarsDatePattern() {
            return DEFAULT_HANDLEBARS_DATE_PATTERN;
        }

        public static String getTemplatesDirectory() {
            return TEMPLATES_DIR;
        }

        public static String getTemplatesSuffix() {
            return TEMPLATES_SUFFIX;
        }

        public static String getMainContentTemplateName() {
            return MAIN_CONTENT_TEMPLATE_NAME;
        }
    }

    /*Phantom JS Configuration*/
    public static class PHANTOMJS {
        private static final String PHANTOMJS_PATH = StringUtils.defaultIfBlank(getValue("PHANTOMJS_PATH"), "/usr/local/bin/phantomjs");

        public static String getPhantomjsPath() {
            return PHANTOMJS_PATH;
        }
    }


    /*Highcharts Image rendering configuration*/
    public static class HIGHCHARTS {
        private final static String SPARKLINE_FILE = "sparklineconfig.js";
        private final static String LINECHART_FILE = "linechartconfig.js";
        private final static String SEARCHCHART_FILE = "searchchartconfig.js";
        private static final String HIGHCHARTS_CONFIG_DIR = "src/main/web/templates/highcharts";
        private static final int MAX_HIGHCHARTS_SERVER_CONNECTION = defaultNumberIfBlank(getNumberValue("HIGHCHARTS_EXPORT_MAX_CONNECTION"), 50);


        //Trailing slash seems to be important. Export server redirects to trailing slash url if not there
        private static final String EXPORT_SEVER_URL = StringUtils.defaultIfBlank(getValue("HIGHCHARTS_EXPORT_SERVER"), "http://localhost:9999/export/");

        public static String getExportSeverUrl() {
            return EXPORT_SEVER_URL;
        }

        public static int getMaxHighchartsServerConnection() {
            return MAX_HIGHCHARTS_SERVER_CONNECTION;
        }
    }

    /**
     * Gets a configured value for the given key from either the system
     * properties or an environment variable.
     * <p/>
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


    /**
     * Use this method to generate new credentials.
     *
     * @param args Not used
     * @throws InvalidKeyException
     */
    public static void main(String[] args) throws InvalidKeyException {

        // Encrypt password:
        String password = "insert password here";
        String base64 = ByteArray.toBase64String(password.getBytes());
        String salt = Random.salt();
        SecretKey key = Keys.newSecretKey();
        String wrappedKey = new KeyWrapper(password, salt).wrapSecretKey(key);

        // Print out the values you'll need to update above:
        System.out.println("base64 key password: " + base64);
        System.out.println("salt: " + salt);
        System.out.println("Wrapped key: " + wrappedKey);

        // And in the run script:
        System.out.println("Encrypted password: " + new Crypto().encrypt("tr3degaR", key));
    }

}
