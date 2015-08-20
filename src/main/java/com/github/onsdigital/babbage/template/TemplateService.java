package com.github.onsdigital.babbage.template;

import com.github.onsdigital.babbage.template.handlebars.HandlebarsRenderer;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.configuration.Configuration;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.github.onsdigital.babbage.util.json.JsonUtil.toMap;

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
    public String renderContent(String... data) throws IOException {
        return renderer.renderContent(toMapArray(data));
    }

    /**
     * Renders data using main template
     *
     * @param stream content data as json stream
     * @return
     * @throws IOException
     */
    public String renderContent(InputStream... stream) throws IOException {
        return renderer.renderContent(toMapArray(stream));
    }

    /**
     * Renders template with given name using given data
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName) throws IOException {
        return renderTemplate(templateName, "");
    }

    /**
     * Renders template with given name using given data
     *
     * @param templateName
     * @param data nullable, must be a json object, arrays are not accepted
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName, String... data) throws IOException {
        return renderer.render(templateName, toMapArray(data));
    }

    /**
     * Renders template with given name using given data
     *
     * @param templateName
     * @param data nullable, must be a json object, arrays are not accepted
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName, InputStream... data) throws IOException {
        return renderer.render(templateName, toMapArray(data));
    }

    private Map<String, Object> getThreadContext() {
        return ThreadContext.getAllData();
    }

    private Map<String, Object>[] toMapArray(String... data) throws IOException {
        Map<String, Object>[] list = null;
        list = ArrayUtils.add(list, getThreadContext());
        for (String s : data) {
            list = ArrayUtils.add(list, toMap(s));
        }
        return list;
    }

    private Map<String, Object>[] toMapArray(InputStream... data) throws IOException {
        Map<String, Object>[] list = null;
        list = ArrayUtils.add(list,getThreadContext());
        for (InputStream s : data) {
            list = ArrayUtils.add(list,toMap(s));
        }
        return list;
    }
}
