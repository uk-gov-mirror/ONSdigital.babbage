package com.github.onsdigital.babbage.template;

import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.babbage.template.handlebars.HandlebarsRenderer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 * <p>
 * Template service includes current {@link com.github.onsdigital.babbage.util.ThreadContext} in the context with key value names
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static TemplateRenderer templateRenderer = new HandlebarsRenderer(Configuration.HANDLEBARS.getTemplatesDirectory(), Configuration.HANDLEBARS.getTemplatesSuffix());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    /**
     * Renders data using main template
     *
     * @param data
     * @return
     * @throws IOException
     */
    public String render(String data) throws IOException {
        return templateRenderer.renderTemplate(data, getThreadContext());
    }

    /**
     * Renders template with given name using given data
     *
     * @param templateName
     * @param data nullable
     * @return
     * @throws IOException
     */
    public String render(String templateName, Object data) throws IOException {
        return templateRenderer.renderTemplate(templateName, data, getThreadContext());
    }


    private Map<String, Object> getThreadContext() {
        return ThreadContext.getAllData();
    }
}
