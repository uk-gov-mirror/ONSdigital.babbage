package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for image and defines how to replace them.
 */
public class ImageTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-image\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    public ImageTagReplacer(String path) {
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
     * Replace a single found instance of the image tag.
     * @param matcher
     * @return
     * @throws IOException
     */
    @Override
    public String replace(Matcher matcher) throws IOException {

        String tagPath = matcher.group(1);
        String figureUri = resolveFigureUri(this.getPath(), Paths.get(tagPath));

        Map<String, Object> additionalFields = new HashMap<>();
        additionalFields.put("parentPageUri", this.getPath().toString());

        try (ContentStream stream = ContentClient.getInstance().getContentStream(figureUri)) {
            return TemplateService.getInstance().renderTemplate("partials/image", stream.getDataStream());
        } catch (ContentReadException e) {
            return TemplateService.getInstance().renderTemplate("partials/image", Serialiser.serialise(new ImageData(figureUri)));
        }
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
