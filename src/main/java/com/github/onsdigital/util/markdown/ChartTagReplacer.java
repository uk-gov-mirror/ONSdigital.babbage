package com.github.onsdigital.util.markdown;

import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.request.handler.ChartRequestHandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class ChartTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

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
     * @param matcher
     * @param zebedeeRequest
     * @return
     * @throws IOException
     */
    @Override
    public String replace(Matcher matcher, ZebedeeRequest zebedeeRequest) throws IOException {

        String uri = matcher.group(1);

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        try {
            return new ChartRequestHandler().getHtml(uri, zebedeeRequest, "partials/chart");
        } catch (ContentNotFoundException e) {
            return matcher.group();
        }
    }
}
