package com.github.onsdigital.template.handlebars.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownCustomTagHelperTest {

    @Test
    public void applyShouldReplaceMatchesWithReplacement() {

        String expected = "some content before chart businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/ some content after the chart";

        // Given some input with a custom markdown tag
        String input = "some content before chart <ons-chart path=\"businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/\" /> some content after the chart";
        DummyTagReplacementStrategy strategy = new DummyTagReplacementStrategy();

        // When the Apply method is called
        String actual = CustomMarkdownTagHelper.replaceCustomTags(input, strategy);

        // Then the result has the tags replaced
        assertEquals(expected, actual);
    }
}
