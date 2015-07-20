package com.github.onsdigital.template.handlebars;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.onsdigital.template.TemplateRenderer;
import com.github.onsdigital.template.handlebars.helpers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bren on 28/05/15.
 */
public class HandlebarsRenderer implements TemplateRenderer {

    private Handlebars handlebars;

    public HandlebarsRenderer(String templatesDirectory, String templatesSuffix) {
        handlebars = new Handlebars(new FileTemplateLoader(templatesDirectory, templatesSuffix)).with(new HighConcurrencyTemplateCache());
        initializeHelpers();
    }

    private void initializeHelpers() {
        handlebars.registerHelper("md", new MarkdownHelper());
        handlebars.registerHelper("df", new DateFormatHelper());
        registerFileHelpers();
        registerConditionHelpers();
        handlebars.registerHelper(ArrayHelpers.contains.name(), ArrayHelpers.contains);
        handlebars.registerHelper(MathHelper.increment.name(), MathHelper.increment);
        handlebars.registerHelper(MathHelper.decrement.name(), MathHelper.decrement);
        handlebars.registerHelper(LoopHelper.NAME, new LoopHelper());
        handlebars.registerHelper(PathHelper.rootpath.name(), PathHelper.rootpath);
        // String helpers
        StringHelpers.register(handlebars);
        // Humanize helpers
        HumanizeHelper.register(handlebars);
    }

    //Compiled templates cache
    private static Map<String, Template> templatesCache = new ConcurrentHashMap<>();

    @Override
    public String renderTemplate(String templateName, Object data) throws IOException {
        return renderTemplate(templateName, data, new HashMap<String, Object>());
    }

    @Override
    public String renderTemplate(String templateName, Object data, Map<String, ?> additionalData) throws IOException {
        Template template = getTemplate(templateName);

        Context context = Context
                .newBuilder(data)
                .combine(additionalData)
                .resolver(FieldValueResolver.INSTANCE, MapValueResolver.INSTANCE)
                .build();
        return template.apply(context);
    }

    private Template getTemplate(String templateName) throws IOException {
        return compileTemplate(templateName);
    }

    private Template compileTemplate(String templateName) throws IOException {
        return handlebars.compile(templateName);
    }

    private void registerConditionHelpers() {
        ConditionHelper[] values = ConditionHelper.values();
        for (int i = 0; i < values.length; i++) {
            ConditionHelper value = values[i];
            handlebars.registerHelper(value.name(), value);
        }
    }

    private void registerFileHelpers() {
        FileHelpers[] values = FileHelpers.values();
        for (int i = 0; i < values.length; i++) {
            FileHelpers value = values[i];
            handlebars.registerHelper(value.name(), value);
        }
    }

}
