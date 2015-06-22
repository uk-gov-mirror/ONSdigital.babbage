package com.github.onsdigital.template.handlebars.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DummyTagReplacementStrategy implements TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    @Override
    public String replace(Matcher matcher) {
        return matcher.group(1);
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }
}
