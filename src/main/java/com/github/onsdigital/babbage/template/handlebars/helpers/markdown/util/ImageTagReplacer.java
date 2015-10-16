package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for image and defines how to replace them.
 */
public class ImageTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-image\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

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
     * Replace a single found instance of the image tag.
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

        return TemplateService.getInstance().renderTemplate("partials/image", Serialiser.serialise(new ImageData(uri)));
    }

    /**
     * Helper class to serialise data required in the template.
     */
    private class ImageData {
        public String uri;

        public ImageData(String uri) {
            this.uri = uri;
        }
    }
}
