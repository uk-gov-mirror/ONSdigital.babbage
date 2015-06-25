package com.github.onsdigital.template;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.template.handlebars.HandlebarsRenderer;

import java.io.IOException;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static TemplateRenderer templateRenderer = new HandlebarsRenderer(Configuration.getTemplatesDirectory(), Configuration.getTemplatesSuffix());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    public String renderPage(Page page) throws IOException {
        System.out.println("Page rendering requested for content type: " + page.getType());
        return templateRenderer.renderTemplate(TemplateMapping.getTemplateName(page.getType()), page);
    }

    public String renderPage(Page page, String templateName) throws IOException {
        return templateRenderer.renderTemplate(templateName, page);
    }
}
