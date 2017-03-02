package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.logging.Log;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for image and defines how to replace them.
 */
public class ImageTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-image\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");
    private final String template;

    public ImageTagReplacer(String path, String template) {
        super(path);
        this.template = template;
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

        try {
            ContentResponse contentResponse = ContentClient.getInstance().getContent(figureUri);
            return TemplateService.getInstance().renderTemplate(template, contentResponse.getDataStream());
        } catch (ResourceNotFoundException e) {
            Log.buildDebug("Failed to find figure data for image.").addParameter("URL", figureUri).log();
            return TemplateService.getInstance().renderTemplate(figureNotFoundTemplate);
        } catch (ContentReadException e) {
            return TemplateService.getInstance().renderTemplate(template, Serialiser.serialise(new ImageData(figureUri)));
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
