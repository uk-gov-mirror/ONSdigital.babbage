package com.github.onsdigital.babbage.search.model.filter;

/**
 * Class representing the filtering based on the first letter.
 */
public class FirstLetterFilter implements Filter {

    private final String key = "withFirstLetter";
    private final String value;

    public FirstLetterFilter(final String firstLetter) {
        this.value = firstLetter;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
