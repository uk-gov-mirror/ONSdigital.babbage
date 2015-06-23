package com.github.onsdigital.template.handlebars.helpers;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.request.handler.DataRequestHandler;
import com.github.onsdigital.template.TemplateService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class ChartTagReplacer implements TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

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

        String uri = matcher.group(1) + ".json";

        // load the chart json data
        DataRequestHandler dataRequestHandler = new DataRequestHandler();
        try {
            Page page = dataRequestHandler.readAsPage(uri, false, null);
            // load the chart template and inject the data.
            String html = TemplateService.getInstance().renderPage(page);
            return html;
        } catch (ContentNotFoundException | DataNotFoundException e) {
            return uri;
        }
    }
}
