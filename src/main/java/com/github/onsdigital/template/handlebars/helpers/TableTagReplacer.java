package com.github.onsdigital.template.handlebars.helpers;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.table.Table;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.request.handler.DataRequestHandler;
import com.github.onsdigital.template.TemplateService;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class TableTagReplacer implements TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-table\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    /**
     * Gets the pattern that this strategy is applied to.
     * @return
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * The function that generates the replacement text for each match.
     * @param matcher
     * @return
     */
    @Override
    public String replace(Matcher matcher) throws IOException {

        String uri = matcher.group(1);

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        Page page;
        try {
            page = new DataRequestHandler().readAsPage(uri, false, null);
        } catch (ContentNotFoundException | DataNotFoundException e) {
            page = new Table();
            page.setUri(URI.create(uri));
        }

        String html = TemplateService.getInstance().render(page, "partials/table");
        return html;
    }
}
