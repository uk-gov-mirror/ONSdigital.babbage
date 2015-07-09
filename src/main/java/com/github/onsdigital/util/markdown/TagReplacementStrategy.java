package com.github.onsdigital.util.markdown;

import com.github.onsdigital.data.zebedee.ZebedeeRequest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for applying custom functions when replacing custom markdown tags.
 */
public abstract class TagReplacementStrategy {


    public String replaceCustomTags(String input, ZebedeeRequest zebedeeRequest) throws IOException {

        Matcher matcher = this.getPattern().matcher(input);
        StringBuffer result = new StringBuffer(input.length());
        while (matcher.find()) {
            matcher.appendReplacement(result, this.replace(matcher, zebedeeRequest));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Gets the pattern that this strategy is applied to.
     * @return
     */
    abstract Pattern getPattern();

    /**
     * The function that generates the replacement text for each match.
     * @param matcher
     * @param zebedeeRequest
     * @return
     */
    abstract String replace(Matcher matcher, ZebedeeRequest zebedeeRequest) throws IOException;
}
