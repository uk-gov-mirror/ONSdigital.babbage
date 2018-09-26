package com.github.onsdigital.babbage.configuration;

import com.github.onsdigital.babbage.logging.LogBuilder;

import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getStringAsBool;
import static com.github.onsdigital.babbage.configuration.EnvVarUtils.getValueOrDefault;

public class Handlebars implements Loggable {

    private static Handlebars INSTANCE;

    private static final String TEMPLATES_DIR_KEY = "TEMPLATES_DIR";
    private static final String TEMPLATES_SUFFIX_KEY = "TEMPLATES_SUFFIX";
    private static final String RELOAD_TEMPLATES_KEY = "RELOAD_TEMPLATES";

    private final String defaultHandlebarsDatePattern;
    private final String mainContentTemplateName;
    private final String mainChartConfigTemplateName;
    private final String templatesDir;
    private final String templatesSuffix;
    private final boolean reloadTemplateChanges;

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

    public void logConfiguration() {
        LogBuilder.Log()
                .parameter("defaultHandlebarsDatePattern", defaultHandlebarsDatePattern)
                .parameter("mainContentTemplateName", mainContentTemplateName)
                .parameter("mainChartConfigTemplateName", mainChartConfigTemplateName)
                .parameter("templatesDir", templatesDir)
                .parameter("templatesSuffix", templatesSuffix)
                .parameter("reloadTemplateChanges", reloadTemplateChanges)
                .info("handlerbars configuration");
    }
}
