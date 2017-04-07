package com.github.onsdigital.babbage.api.endpoint.rss.builder;

import com.github.onsdigital.babbage.api.endpoint.rss.RssSearchFilter;
import com.github.onsdigital.babbage.api.endpoint.rss.service.PropertiesService;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Builder wrapper for the {@link SyndFeed} object providing convenient way to construct {@link SyndFeed} objects.
 */
public class SyndFeedBuilder {

    private String defaultTitle;

    private SyndFeed feed = new SyndFeedImpl();
    private PropertiesService propertiesService;

    public SyndFeedBuilder() {
        this.propertiesService = PropertiesService.getInstance();
        this.defaultTitle = propertiesService.get("rss.title.default");
    }

    public static void writeFeedToResponse(SyndFeed feed, HttpServletResponse response) {
        try {
            new SyndFeedOutput().output(feed, response.getWriter());
        }
        catch (IOException | FeedException ex) {
            System.out.println("Unexpected error while attempting to write RSS feed to HttpServletResponse: "
                                       + ex.getMessage());
        }
    }

    public SyndFeedBuilder type(String type) {
        feed.setFeedType(type);
        return this;
    }

    public SyndFeedBuilder title(RssSearchFilter filter) {
        feed.setTitle(getTitle(filter.getProvidedParams()));
        return this;
    }

    public SyndFeedBuilder title(String title) {
        feed.setTitle(title);
        return this;
    }

    public SyndFeedBuilder link(String link) {
        feed.setLink(link);
        return this;
    }

    public SyndFeedBuilder desc(String desc) {
        feed.setDescription(desc);
        return this;
    }

    public SyndFeedBuilder entries(List<SyndEntry> entries) {
        feed.setEntries(entries);
        return this;
    }

    public SyndFeedBuilder category(String category) {
        SyndCategoryImpl syndCat = new SyndCategoryImpl();
        syndCat.setName(category);
        feed.setCategories(Arrays.asList(new SyndCategory[]{syndCat}));
        return this;
    }

    public SyndFeed build() {
        this.feed.setEncoding("ISO-8859-1");
        this.feed.setDescription(defaultTitle);
        return this.feed;
    }

    private String getTitle(Map<String, String> filterParams) {
        StringBuilder title = new StringBuilder(defaultTitle);
        if (!filterParams.isEmpty()) {
            List<String> args = filterParams.entrySet()
                                            .stream()
                                            .map(entry -> {
                                                final String key = entry.getKey();
                                                final String value = entry.getValue();
                                                final String pattern = propertiesService.get(key);
                                                return String.format(pattern, value);
                                            })
                                            .collect(Collectors.toList());

            title.append(" ")
                 .append(String.format(propertiesService.get("rss.title.specific"), formatList(args)));
        }
        return title.toString();
    }

    private String formatList(List<String> list) {
        return join(list, ", ");

    }
}
