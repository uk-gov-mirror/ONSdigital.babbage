package com.github.onsdigital.babbage.locale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Provides cached access to locale specific labels as maps.
 */
public class LocaleConfig {

    private static String defaultLanguage = "en";
    private static Locale english = Locale.ENGLISH;
    private static List<String> supportedLanguages = new ArrayList<>(Arrays.asList(defaultLanguage, "cy"));

    private static Map<Locale, Map<String, String>> localeToLabels = new HashMap<>();
    private static Map<String, Locale> languageCodeToLocale = new HashMap<>();

    static {
        for (String supportedLanguage : supportedLanguages) {
            languageCodeToLocale.put(supportedLanguage, new Locale(supportedLanguage));
        }
    }

    /**
     * Fetch a map of labels for a given locale.
     *
     * @param locale
     * @return
     */
    public static Map<String, String> getLabels(Locale locale) {
// disabled caching while in development
//        if (!localeToLabels.containsKey(locale))
//            synchronized (LocaleConfig.class) {
//                if (!localeToLabels.containsKey(locale)) {
//                    // resource bundle class used under the covers to automate the selection of the bundle file for the given
//                    // locale. The supported locales are defined by the available bundle property files.
//                    Properties properties = loadProperties(locale);
//                    Map<String, String> labels = toMap(properties);
//                    localeToLabels.put(locale, labels);
//                }
//            }
//
//        return localeToLabels.get(locale);

        Properties properties = loadProperties(locale);
        Map<String, String> labels = toMap(properties);
        return labels;
    }

    private static Properties loadProperties(Locale locale) {
        Properties properties;

        try {
            try {
                if (locale.equals(english)) {
                    properties = LoadProperties("LabelsBundle.properties");
                } else {
                    properties = LoadProperties("LabelsBundle_" + locale.getLanguage() + ".properties");
                }

            } catch (IOException e) {
                properties = LoadProperties("LabelsBundle.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load search properties file", e);
        }
        return properties;
    }

    private static Properties LoadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        InputStream utf8in = LocaleConfig.class.getClassLoader().getResourceAsStream(filename);
        Reader reader = new InputStreamReader(utf8in, "UTF-8");
        properties.load(reader);
        return properties;
    }


    /**
     * return the locale instance for the given language code.
     * @return
     */
    public static Collection<Locale> getSupportedLanguages() {
        return languageCodeToLocale.values();
    }

    public static Locale getDefaultLocale() {
        return english;
    }

    private static Map<String, String> toMap(ResourceBundle resourceBundle) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resourceBundle.getString(key));
        }

        return map;
    }

    private static Map<String, String> toMap(Properties properties) {
        Map<String, String> map = new HashMap<>();

        Enumeration<?> keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            map.put(key, properties.getProperty(key));
        }

        return map;
    }
}
