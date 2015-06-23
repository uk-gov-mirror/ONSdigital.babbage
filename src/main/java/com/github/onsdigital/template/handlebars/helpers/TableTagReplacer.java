package com.github.onsdigital.template.handlebars.helpers;

import com.github.onsdigital.request.handler.TableRequestHandler;

import java.io.IOException;
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

        // load the chart json data
        TableRequestHandler requestHandler = new TableRequestHandler();
        try {
            String html = requestHandler.getHtml(uri, null);
            return html;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return uri;
        }
//        String uri = matcher.group(1);
//
//        // load the chart json data
//        DataRequestHandler dataRequestHandler = new DataRequestHandler();
//        try {
//            Page page = dataRequestHandler.readAsPage(uri, false, null);
//
//            String html = TemplateService.getInstance().renderPage(page);
//            return html;
//        } catch (ContentNotFoundException | DataNotFoundException e) {
//            return uri;
//        }
    }
}
