package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class TableTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-table\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    public TableTagReplacer(String path) {
        super(path);
    }

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

        String tagPath = matcher.group(1);
        String figureUri = resolveFigureUri(this.getPath(), Paths.get(tagPath));

        try {
            ContentResponse contentResponse = ContentClient.getInstance().getContent(figureUri);
            String result = TemplateService.getInstance().renderTemplate("partials/table", contentResponse.getDataStream());
            return result;
        } catch (ContentReadException e) {
            System.err.println("Failed rendering table, uri:" + figureUri);
            return matcher.group();
        }
    }
}
