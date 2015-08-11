package com.github.onsdigital.template;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.template.handlebars.HandlebarsRenderer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static TemplateRenderer templateRenderer = new HandlebarsRenderer(Configuration.HANDLEBARS.getTemplatesDirectory(), Configuration.HANDLEBARS.getTemplatesSuffix());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    public String renderPage(Page page) throws IOException {
        System.out.println("Page rendering requested for content type: " + page.getType());
        return templateRenderer.renderTemplate(page);
    }

    public String renderPage(Page page, Map<String, Object> additionalData) throws IOException {
        System.out.println("Page rendering requested for content type: " + page.getType());
        return templateRenderer.renderTemplate(page, additionalData);
    }

    /**
     *Object as json file or data
     *
     * @param templateName
     * @param data
     * @return
     * @throws IOException
     */
    public String render(String templateName,Object data) throws IOException {
        return templateRenderer.renderTemplate( templateName, data);
    }

    public String render(String templateName, Object data, Map<String, Object> additionalData) throws IOException {
        return templateRenderer.renderTemplate(templateName, data, additionalData);
    }
}
