package com.github.onsdigital.babbage.template.handlebars;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.onsdigital.babbage.template.TemplateRenderer;
import com.github.onsdigital.babbage.template.handlebars.helpers.*;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.configuration.Configuration.HANDLEBARS.getMainContentTemplateName;

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
        // String helpers
        StringHelpers.register(handlebars);
        // Humanize helpers
        HumanizeHelper.register(handlebars);
        registerHelpers();
    }

    @Override
    public String renderTemplate(Object data, Map<String, Object> additionalData) throws IOException {
        return renderTemplate(getMainContentTemplateName(), data, additionalData);
    }

    @Override
    public String renderTemplate(String templateName, Object data, Map<String, Object> additionalData) throws IOException {
        Template template = getTemplate(templateName);

        Object inputData = data instanceof String ? toJsonNode(data) : data;

        Context.Builder builder = Context
                .newBuilder(inputData)
                .resolver(JsonNodeValueResolver.INSTANCE,
                        FieldValueResolver.INSTANCE,
                        MapValueResolver.INSTANCE
                );

        if (additionalData != null) {
            for (Map.Entry<String, Object> entry : additionalData.entrySet()) {
                builder.combine(entry.getKey(), entry.getValue());
            }
        }
        return template.apply(builder.build());
    }


    private Template getTemplate(String templateName) throws IOException {
        return compileTemplate(templateName);
    }

    private Template compileTemplate(String templateName) throws IOException {
        return handlebars.compile(templateName);
    }

    private void registerHelpers() {
        System.out.println("Resolving Handlebars helpers");
        try {

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().addUrls(HandlebarsRenderer.class.getProtectionDomain().getCodeSource().getLocation());
            configurationBuilder.addClassLoader(HandlebarsRenderer.class.getClassLoader());
            Set<Class<? extends BabbageHandlebarsHelper>> classes = new Reflections(configurationBuilder).getSubTypesOf(BabbageHandlebarsHelper.class);

            for (Class<? extends BabbageHandlebarsHelper> helperClass : classes) {
                String className = helperClass.getSimpleName();
                boolean _abstract = Modifier.isAbstract(helperClass.getModifiers());
                if (_abstract && !helperClass.isEnum()) {
                    System.out.println("Skipping registering abstract handlebars helper " + className);
                    continue;
                }

                if (helperClass.isEnum()) {
                    BabbageHandlebarsHelper[] helpers = helperClass.getEnumConstants();
                    for (BabbageHandlebarsHelper helper : helpers) {
                        System.out.println("Registering Handlebars helper " + helper.getHelperName() + ":" + helper);
                        handlebars.registerHelper(helper.getHelperName(), helper);
                    }
                } else {
                    //enum constant classes are anonymous classes that are already registered above by getting constants above
                    if (helperClass.isAnonymousClass()) {
                        continue;
                    }
                    BabbageHandlebarsHelper helperInstance = helperClass.newInstance();
                    System.out.println("Registering Handlebars helper  " + helperInstance.getHelperName() + ":" + className);
                    handlebars.registerHelper(helperInstance.getHelperName(), helperInstance);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed initializing handlebars helpers");
            throw new RuntimeException("Failed initializing request handlers", e);
        }

    }

    private JsonNode toJsonNode(Object data) throws IOException {
        return new ObjectMapper().readValue(String.valueOf(data), JsonNode.class);
    }
}
