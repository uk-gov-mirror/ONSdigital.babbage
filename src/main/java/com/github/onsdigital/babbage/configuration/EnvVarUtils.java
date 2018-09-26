package com.github.onsdigital.babbage.configuration;

import org.apache.commons.lang3.StringUtils;

public class EnvVarUtils {

    private static final String Y = "Y";

    private EnvVarUtils() {
        // hide constructor.
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
    public static String getValue(String key) {
        return StringUtils.defaultIfBlank(System.getProperty(key), System.getenv(key));
    }

    public static String getValueOrDefault(String key, String defaultVal) {
        return StringUtils.defaultIfBlank(System.getProperty(key), defaultVal);
    }

    public static Integer getNumberValue(String key) {
        String value = getValue(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return Integer.valueOf(value.trim());
    }


    public static Integer defaultNumberIfBlank(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * One of the Babbage "specials". For reasons unknown to anyone flag env vars use "Y" or "N" instead of "true" and
     * "false" (sigh...). Method attempts to get the value and convert to a boolean or use a default if it doesn't
     * exist.
     *
     * There is a TODO to fix this and use a sane approach.
     */
    public static boolean getStringAsBool(String key, String defaultVal) {
        return Y.equals(StringUtils.defaultIfBlank(getValue(key), defaultVal));
    }
}
