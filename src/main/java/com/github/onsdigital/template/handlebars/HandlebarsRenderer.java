package com.github.onsdigital.template.handlebars;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.template.TemplateRenderer;
import com.github.onsdigital.template.handlebars.helpers.DateFormatHelper;
import com.github.onsdigital.template.handlebars.helpers.CustomMarkdownTagHelper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bren on 28/05/15.
 */
public class HandlebarsRenderer implements TemplateRenderer {

    private Handlebars handlebars;

    public HandlebarsRenderer(String templatesDirectory, String templatesSuffix) {
        handlebars = new Handlebars(new FileTemplateLoader(Configuration.getTemplatesDirectory(), Configuration.getTemplatesSuffix()));
        initializeHelpers();
    }

    private void initializeHelpers() {
        handlebars.registerHelper("md", new CustomMarkdownTagHelper());
        handlebars.registerHelper("df", new DateFormatHelper());
        // String helpers
        StringHelpers.register(handlebars);
        // Humanize helpers
        HumanizeHelper.register(handlebars);
    }

    //Compiled templates cache
    private static Map<String, Template> templatesCache = new ConcurrentHashMap<>();

    @Override
    public String renderTemplate(String templateName, Page data) throws IOException {
        Template template = getTemplate(templateName);

        Context context = Context
                .newBuilder(data)
                .resolver(FieldValueResolver.INSTANCE, MapValueResolver.INSTANCE)
                .build();
        return template.apply(context);
    }

    private Template getTemplate(String templateName) throws IOException {
        return compileTemplate(templateName);

//        Template template = templatesCache.get(templateName);
//        if (template == null) {
//            template = compileTemplate(templateName);
//            templatesCache.put(templateName, template);
//        }
//        return template;
    }

    private Template compileTemplate(String templateName) throws IOException {
        System.out.println("Compiling template for " + templateName + " for the first time");
        return handlebars.compile(templateName);
    }

}
