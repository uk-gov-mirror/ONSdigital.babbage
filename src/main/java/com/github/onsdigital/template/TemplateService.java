package com.github.onsdigital.template;

import com.github.onsdigital.content.base.Content;
import com.github.onsdigital.content.base.ContentType;
import com.github.onsdigital.content.serialiser.ContentSerialiser;
import com.github.onsdigital.content.taxonomy.ProductPage;
import com.github.onsdigital.content.taxonomy.TaxonomyLandingPage;
import com.github.onsdigital.template.handlebars.HandlebarsRenderer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static TemplateRenderer templateRenderer = new HandlebarsRenderer();

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    public String renderPage(String data) throws IOException {
        JsonObject object = new JsonParser().parse(data).getAsJsonObject();
        String contentType = object.get("type").getAsString();
        System.out.println("Page rendering requested for content type: " + contentType);
        return templateRenderer.renderTemplate(TemplateMapping.getTemplateName(ContentType.valueOf(contentType)) , new ContentSerialiser().deserialise(data, ProductPage.class));
    }

}
