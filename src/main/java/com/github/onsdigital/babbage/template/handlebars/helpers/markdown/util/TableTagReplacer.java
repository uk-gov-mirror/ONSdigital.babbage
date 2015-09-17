package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.URIUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class TableTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-table\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    /**
     * Gets the pattern that this strategy is applied to.
     *
     * @return
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * The function that generates the replacement text for each match.
     *
     * @param matcher
     * @return
     * @throws IOException
     */
    @Override
    public String replace(Matcher matcher) throws IOException {

        String uri = matcher.group(1);

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        try (
                ContentStream json = ContentClient.getInstance().getContentStream(uri);
                ContentStream html = ContentClient.getInstance().getResource(URIUtil.cleanUri(uri) + ".html")
        ) {

            LinkedHashMap<String, Object> htmlEntry = new LinkedHashMap<>();
            htmlEntry.put("html", html.getAsString());
            String jsonString = json.getAsString();
            String result = TemplateService.getInstance().renderTemplate("partials/table", jsonString, htmlEntry);
            return result;

        } catch (ContentReadException e) {
            System.err.println("Failed rendering table, uri:" + uri);
            return matcher.group();
        }
    }
}
