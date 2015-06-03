package com.github.onsdigital.template.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.template.TemplateRenderer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bren on 28/05/15.
 */
public class HandlebarsRenderer implements TemplateRenderer {

    private static FileTemplateLoader templateLoader = new FileTemplateLoader(Configuration.getTemplatesDirectory(), Configuration.getTemplatesSuffix());

    //Compiled templates cache
    private static Map<String, Template> templatesCache = new ConcurrentHashMap<>();

    @Override
    public String renderTemplate(String templateName, String data) throws IOException {
        Template template = getTemplate(templateName);
        return template.apply(data);
    }

    private Template getTemplate(String templateName) throws IOException {
        Template template = templatesCache.get(templateName);
        if (template == null) {
            template = compileTemplate(templateName);
            templatesCache.put(templateName, template);
        }
        return template;
    }

    private Template compileTemplate(String templateName) throws IOException {
        System.out.println("Compiling template for " + templateName + " for the first time");
        return new Handlebars(templateLoader).compile(templateName);
    }


}
