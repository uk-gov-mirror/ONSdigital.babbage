package com.github.onsdigital.babbage.configuration;

import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getStringAsBool;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValue;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class Handlebars implements AppConfig {

    private static Handlebars INSTANCE;

    private static final String TEMPLATES_DIR_KEY = "TEMPLATES_DIR";
    private static final String TEMPLATES_SUFFIX_KEY = "TEMPLATES_SUFFIX";
    private static final String RELOAD_TEMPLATES_KEY = "RELOAD_TEMPLATES";
    private static final String ENABLE_COOKIES_CONTROL = "ENABLE_COOKIES_CONTROL";

    private final String defaultHandlebarsDatePattern;
    private final String mainContentTemplateName;
    private final String mainChartConfigTemplateName;
    private final String templatesDir;
    private final String templatesSuffix;
    private final boolean reloadTemplateChanges;
    private final boolean enableCookiesControl;

    static Handlebars getInstance() {
        if (INSTANCE == null) {
            synchronized (Handlebars.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Handlebars();
                }
            }
        }
        return INSTANCE;
    }

    private Handlebars() {
        defaultHandlebarsDatePattern = "d MMMM yyyy";
        mainContentTemplateName = "main";
        mainChartConfigTemplateName = "chart-config";

        templatesDir = getValueOrDefault(TEMPLATES_DIR_KEY, "target/web/templates/handlebars");
        templatesSuffix = getValueOrDefault(TEMPLATES_SUFFIX_KEY, ".handlebars");
        reloadTemplateChanges = getStringAsBool(RELOAD_TEMPLATES_KEY, "N");
        enableCookiesControl = Boolean.parseBoolean(getValue(ENABLE_COOKIES_CONTROL));
    }

    public String getHandlebarsDatePattern() {
        return defaultHandlebarsDatePattern;
    }

    public String getTemplatesDirectory() {
        return templatesDir;
    }

    public String getTemplatesSuffix() {
        return templatesSuffix;
    }

    public String getMainContentTemplateName() {
        return mainContentTemplateName;
    }

    public String getMainChartConfigTemplateName() {
        return mainChartConfigTemplateName;
    }

    public boolean isReloadTemplateChanges() {
        return reloadTemplateChanges;
    }

    public boolean isEnableCookiesControl() {
        return enableCookiesControl;
    }

    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("defaultHandlebarsDatePattern", defaultHandlebarsDatePattern);
        config.put("mainContentTemplateName", mainContentTemplateName);
        config.put("mainChartConfigTemplateName", mainChartConfigTemplateName);
        config.put("templatesDir", templatesDir);
        config.put("templatesSuffix", templatesSuffix);
        config.put("reloadTemplateChanges", reloadTemplateChanges);
        config.put("enableCookiesControl", enableCookiesControl);
        return config;
    }
}
