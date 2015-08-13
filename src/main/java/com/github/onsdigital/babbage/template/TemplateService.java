package com.github.onsdigital.babbage.template;

import com.github.onsdigital.babbage.template.handlebars.HandlebarsRenderer;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.configuration.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.github.onsdigital.babbage.util.JsonUtil.deserialiseObject;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 * <p>
 * Template service includes current {@link com.github.onsdigital.babbage.util.ThreadContext} in the context with key value names
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static HandlebarsRenderer renderer = new HandlebarsRenderer(Configuration.HANDLEBARS.getTemplatesDirectory(), Configuration.HANDLEBARS.getTemplatesSuffix());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    /**
     * Renders data using main template
     *
     * @param data  content data as json
     * @return
     * @throws IOException
     */
    public String renderContent(String data) throws IOException {
        return renderer.renderContent(deserialiseObject(data), getThreadContext());
    }

    /**
     * Renders data using main template
     *
     * @param stream content data as json stream
     * @return
     * @throws IOException
     */
    public String renderContent(InputStream stream) throws IOException {
        return renderer.renderContent(deserialiseObject(stream), getThreadContext());
    }

    /**
     * Renders template with given name using given data
     *
     * @param templateName
     * @param data nullable, must be a json object, arrays are not accepted
     * @return
     * @throws IOException
     */
    public String render(String templateName, String data) throws IOException {
        return renderer.render(templateName, deserialiseObject(data), getThreadContext());
    }

    private Map<String, Object> getThreadContext() {
        return ThreadContext.getAllData();
    }
}
