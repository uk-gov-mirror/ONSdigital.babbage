package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for applying custom functions when replacing custom markdown tags.
 */
public abstract class TagReplacementStrategy {

    private final Path path;
    static final String figureNotFoundTemplate = "partials/figureNotFound";

    /**
     * Create an instance of a tag replacement strategy with the given page path.
     * <p>
     * The page path is the path of the page having tags replaced. It is used to resolve links that are relative to the page.
     *
     * @param path
     */
    public TagReplacementStrategy(String path) {
        this.path = Paths.get(path);
    }

    public String replaceCustomTags(String input) throws IOException {

        Matcher matcher = this.getPattern().matcher(input);
        StringBuffer result = new StringBuffer(input.length());
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(this.replace(matcher)));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Gets the pattern that this strategy is applied to.
     *
     * @return
     */
    abstract Pattern getPattern();

    /**
     * The function that generates the replacement text for each match.
     *
     * @param matcher
     * @return
     */
    abstract String replace(Matcher matcher) throws IOException;

    /**
     * Get the associated path of the page having tags replaced.
     * @return
     */
    public Path getPath() {
        return path;
    }

    /**
     * Resolve the URI using the filename of the markdown tag, and the current page URI.
     * @param tagPath
     * @return
     */
    public String resolveFigureUri(Path pagePath, Path tagPath) {

        String figureUri = pagePath.resolve(tagPath.getFileName()).toString();

        if (!figureUri.startsWith("/")) {
            figureUri = "/" + tagPath;
        }

        return figureUri;
    }
}
