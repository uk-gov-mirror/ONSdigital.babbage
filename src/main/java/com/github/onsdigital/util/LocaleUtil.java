package com.github.onsdigital.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Provides cached access to locale specific labels as maps.
 */
public class LocaleUtil {

    private static String defaultLanguage = "en";
    private static List<String> supportedLanguages = new ArrayList<>(Arrays.asList(defaultLanguage, "cy"));

    private static Map<Locale, Map<String, String>> localeToLabels = new HashMap<>();
    private static Map<String, Locale> languageCodeToLocale = new HashMap<>();

    static {
        for (String supportedLanguage : supportedLanguages) {
            languageCodeToLocale.put(supportedLanguage, new Locale(supportedLanguage));
        }
    }

    /**
     * Given a uri string determine the locale.
     * @param uri
     * @return
     */
    public static Locale getLocaleFromUri(String uri) {
        String language = getLanguage(uri);
        return getLocale(language);
    }

    /**
     * Fetch a map of labels for a given locale.
     *
     * @param locale
     * @return
     */
    public static Map<String, String> getLabels(Locale locale) {

//        if (!localeToLabels.containsKey(locale))
//            synchronized (LocaleUtil.class) {
//                if (!localeToLabels.containsKey(locale)) {
//                    // resource bundle class used under the covers to automate the selection of the bundle file for the given
//                    // locale. The supported locales are defined by the available bundle property files.
//                    ResourceBundle labelsResourceBundle = ResourceBundle.getBundle("LabelsBundle", locale);
//                    Map<String, String> labels = toMap(labelsResourceBundle);
//                    ResourceBundle.clearCache(); // clear the internal cache of resource bundle as we are caching as maps.
//                    localeToLabels.put(locale, labels);
//                }
//            }
//
//        return localeToLabels.get(locale);

        ResourceBundle labelsResourceBundle = ResourceBundle.getBundle("LabelsBundle", locale);
        Map<String, String> labels = toMap(labelsResourceBundle);
        ResourceBundle.clearCache(); // clear the internal cache of resource bundle as we are caching as maps.
        return labels;
    }


    /**
     * Remove the language portion of the given uri if there is one.
     * @param uri
     * @return
     */
    public static String trimLanguage(String uri) {

        if ("/".equals(uri)) {
            return uri;
        }

        Path path = Paths.get(uri);

        if (!hasLanguage(path)) {
            return uri;
        }

        // if the path count is 1 then only the language is in the path, so return "/"
        if (path.getNameCount() < 2) {
            return "/";
        }

        String trimmed = uri;

        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1, trimmed.length());
        }

        trimmed = trimmed.substring(trimmed.indexOf("/"), trimmed.length());

        return trimmed;
    }

    /**
     * Get the language recognised from the given uri. If no language is found then use the default.
     * @param uri
     * @return
     */
    public static String getLanguage(String uri) {

        if ("/".equals(uri)) {
            return defaultLanguage;
        }

        Path path = Paths.get(uri);

        String firstSection = path.getName(0).toString();

        if (LocaleUtil.supportedLanguages.contains(firstSection)) {
            return firstSection;
        }

        return defaultLanguage;
    }


    /**
     * Return true if a language is recognised on the front of the given path.
     * @param path
     * @return
     */
    private static boolean hasLanguage(Path path) {

        String firstSection = path.getName(0).toString();

        if (LocaleUtil.supportedLanguages.contains(firstSection)) {
            return true;
        }

        return false;
    }

    /**
     * return the locale instance for the given language code.
     *
     * @param language
     * @return
     */
    private static Locale getLocale(String language) {
        if (supportedLanguages.contains(language)) {
            return languageCodeToLocale.get(language);
        }
        return languageCodeToLocale.get(defaultLanguage);
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
}
