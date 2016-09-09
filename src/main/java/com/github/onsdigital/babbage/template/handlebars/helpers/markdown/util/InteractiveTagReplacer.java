package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by crispin on 11/12/2015.
 */
public class InteractiveTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-interactive\\surl=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");
    private final String template;

    /**
     * Create an instance of a tag replacement strategy with the given page path.
     * <p>
     * The page path is the path of the page having tags replaced. It is used to resolve links that are relative to the page.
     *
     * @param path
     */
    public InteractiveTagReplacer(String path, String template) {
        super(path);
        this.template = template;
    }

    @Override
    Pattern getPattern() {
        return pattern;
    }

    @Override
    String replace(Matcher matcher) throws IOException {
        String tagPath = matcher.group(1);
        LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
        additionalData.put("id", UUID.randomUUID().toString().substring(10));
        additionalData.put("url", tagPath);
        return TemplateService.getInstance().renderTemplate(template, null, additionalData);
    }
}
