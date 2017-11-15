package com.github.onsdigital.babbage.template;

import com.github.onsdigital.babbage.template.handlebars.HandlebarsRenderer;
import com.github.onsdigital.babbage.util.ThreadContext;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.Configuration.HANDLEBARS.getMainChartConfigTemplateName;
import static com.github.onsdigital.babbage.configuration.Configuration.HANDLEBARS.getMainContentTemplateName;
import static com.github.onsdigital.babbage.configuration.Configuration.HANDLEBARS.getTemplatesDirectory;
import static com.github.onsdigital.babbage.configuration.Configuration.HANDLEBARS.getTemplatesSuffix;
import static com.github.onsdigital.babbage.configuration.Configuration.HANDLEBARS.isReloadTemplateChanges;
import static com.github.onsdigital.babbage.util.json.JsonUtil.toMap;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 * <p/>
 * Template service includes current {@link com.github.onsdigital.babbage.util.ThreadContext} in the context with key value names
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static HandlebarsRenderer renderer = new HandlebarsRenderer(getTemplatesDirectory(), getTemplatesSuffix(), isReloadTemplateChanges());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    /**
     * Renders data using main template, current ThreadContext data is added to context as additional data
     *
     * @param data           Main data to render template with
     * @param additionalData optional additional data map, map keys will be set as the object name when combined with main data
     * @return
     * @throws IOException
     */
    public String renderContent(Object data, Map<String, Object>... additionalData) throws IOException {
        return renderer.render(getMainContentTemplateName(), sanitize(data), addThreadContext(additionalData));
    }

    /**
     * Renders chart configuration using main chart configuration template, current ThreadContext data is added to context as additional data
     *
     * @param data           Main data to render template with
     * @param additionalData optional additional data map, map keys will be set as the object name when combined with main data
     * @return
     * @throws IOException
     */
    public String renderChartConfiguration(Object data, Map<String, Object>... additionalData) throws IOException {
        return renderer.render(getMainChartConfigTemplateName(), sanitize(data), addThreadContext(additionalData));
    }

    /**
     * Renders template with no data other than current thread context
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName) throws IOException {
        return renderer.render(templateName, Collections.emptyMap(), ThreadContext.getAllData());
    }

    /**
     * Renders template with given name using given data, current ThreadContext data is added to context as additional data
     *
     * @param templateName
     * @param data
     * @param additionalData optional additional data
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName, Object data, Map<String, Object>... additionalData) throws IOException {
        return renderer.render(templateName, sanitize(data), addThreadContext(additionalData));
    }

    /*Converts object into map if json string or json input stream*/
    public static Object sanitize(Object data) throws IOException {
        if (data instanceof String) {
            return toMap((String) data);
        } else if (data instanceof InputStream) {
            return toMap((InputStream) data);
        } else {
            return data;
        }
    }

    /**
     * Add current thread context to map array
     *
     * @param data
     * @return
     */
    private Map<String, Object>[] addThreadContext(Map<String, Object>... data) {
        return ArrayUtils.add(data, ThreadContext.getAllData());
    }

}
