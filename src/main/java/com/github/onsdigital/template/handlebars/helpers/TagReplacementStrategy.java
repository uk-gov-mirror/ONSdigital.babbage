package com.github.onsdigital.template.handlebars.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for applying custom functions when replacing custom markdown tags.
 */
public interface TagReplacementStrategy {

    /**
     * Gets the pattern that this strategy is applied to.
     * @return
     */
    Pattern getPattern();

    /**
     * The function that generates the replacement text for each match.
     * @param matcher
     * @return
     */
    String replace(Matcher matcher);

}
