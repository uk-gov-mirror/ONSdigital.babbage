package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.page.statistics.document.bulletin.Bulletin;
import com.github.onsdigital.content.page.statistics.document.bulletin.BulletinDescription;
import com.github.onsdigital.content.partial.Contact;
import com.github.onsdigital.generator.ContentNode;
import com.github.onsdigital.generator.data.Data;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BulletinMarkdown {

    static final String resourceName = "/bulletins";
    public static Map<String, Bulletin> bulletins = new HashMap<>();

    public static void parse() throws IOException {
        Collection<Path> files = Markdown.getFiles(resourceName);

        for (Path file : files) {

            // Read the bulletin:
            Bulletin bulletin = readBulletin(file);

            // Set the URI if necessary:
            ContentNode folder = Data.getFolder(bulletin.getDescription().theme, bulletin.getDescription().level2, bulletin.getDescription().level3);
            if (bulletin.getUri() == null) {
                bulletin.setUri(toUri(folder, bulletin));
            }

            // Add it to the taxonomy:
            folder.bulletins.add(bulletin);
            bulletins.put(bulletin.getDescription().getTitle(), bulletin);
            if (StringUtils.isNotBlank(bulletin.getDescription().getHeadline1())) {
                folder.headlineBulletin = bulletin;
            }
        }
    }

    static Bulletin readBulletin(Path file) throws IOException {

        // Read the file
        System.out.println("Processing bulletin from: " + file);
        Markdown markdown = new Markdown(file);

        // Set up the bulletin
        Bulletin bulletin = new Bulletin();
        BulletinDescription description = new BulletinDescription();

        description.setTitle(markdown.title);
        setDescription(description, markdown);
        bulletin.setSections(markdown.sections);
        bulletin.getSections().addAll(markdown.accordion);

        bulletin.setDescription(description);

        return bulletin;
    }

    /**
     * Reads the "header" information about the bulletin. Information is
     * expected in the form "key : value" and the header block should be
     * terminated with an empty line. The recognised keys are as follows.
     * <ul>
     * <li>Theme</li>
     * <li>Level 2</li>
     * <li>Level 3</li>
     * <li>Summary</li>
     * <li>Headline 1</li>
     * <li>Headline 2</li>
     * <li>Headline 3</li>
     * <li>Contact title</li>
     * <li>Contact email</li>
     * <li>Phone</li>
     * <li>Search keywords</li>
     * <li>National statistic</li>
     * <li>Language</li>
     * <li>Release Date</li>
     * </ul>
     *
     * @param bulletinDescription The {@link BulletinDescription} description to be filled.
     * @param markdown The parsed {@link Markdown}.
     */
    private static void setDescription(BulletinDescription bulletinDescription, Markdown markdown) {

        Map<String, String> properties = markdown.properties;

        // Location
        bulletinDescription.theme = StringUtils.defaultIfBlank(properties.remove("theme"), null);
        bulletinDescription.level2 = StringUtils.defaultIfBlank(properties.remove("level 2"), null);
        bulletinDescription.level3 = StringUtils.defaultIfBlank(properties.remove("level 3"), null);

        // Additional details
        bulletinDescription.setSummary(StringUtils.defaultIfBlank(properties.remove("summary"), null));
        bulletinDescription.setHeadline1(StringUtils.defaultIfBlank(properties.remove("headline 1"), null));
        bulletinDescription.setHeadline2(StringUtils.defaultIfBlank(properties.remove("headline 2"), null));
        bulletinDescription.setHeadline3(StringUtils.defaultIfBlank(properties.remove("headline 3"), null));

        //Contact info
        Contact contact = new Contact();
        contact.setName(StringUtils.defaultIfBlank(properties.remove("contact title"), null));
        contact.setEmail(StringUtils.defaultIfBlank(properties.remove("contact email"), null));
        contact.setTelephone(StringUtils.defaultIfBlank(properties.remove("phone"), null));
        bulletinDescription.setContact(contact);

        //TODO: Where is next release?
        Date releaseDate = toDate(properties.remove("release date"));
        bulletinDescription.setReleaseDate(releaseDate == null ? null : releaseDate);

        // Additional fields for migration:

        bulletinDescription.setNationalStatistic(BooleanUtils.toBoolean(StringUtils.defaultIfBlank(properties.remove("national statistics"), "yes")));
        bulletinDescription.setLanguage(StringUtils.defaultIfBlank(properties.remove("language"), null));
        // Split keywords by commas:
        String searchKeywordsString = StringUtils.defaultIfBlank(properties.remove("search keywords"), "");
        String[] keywords = StringUtils.split(searchKeywordsString, ',');
        List<String> searchKeywords = new ArrayList<String>();
        if (keywords != null) {
            for (int i = 0; i < keywords.length; i++) {
                searchKeywords.add(StringUtils.trim(keywords[i]));
            }
        }
        bulletinDescription.setKeywords(searchKeywords);

        // Note any unexpected information
        for (String property : properties.keySet()) {
            System.out.println("Bulletin key not recognised: '" + property + "' (length " + property.length() + " for value '" + properties.get(property) + "')");
        }

    }

    static URI toUri(ContentNode folder, Bulletin bulletin) {
        URI result = null;

        if (bulletin != null) {
            if (bulletin.getUri() == null) {
                String baseUri = "/" + folder.filename();
                ContentNode parent = folder.parent;
                while (parent != null) {
                    baseUri = "/" + parent.filename() + baseUri;
                    parent = parent.parent;
                }
                baseUri += "/bulletins";
                bulletin.setUri(URI.create(baseUri + "/" + StringUtils.trim(toFilename(bulletin))));
            }
            result = bulletin.getUri();
        }

        return result;
    }

    /**
     * Sanitises an article title to <code>[a-zA-Z0-9]</code>.
     *
     * @return A sanitised string.
     */
    public static String toFilename(Bulletin bulletin) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bulletin.getDescription().getTitle().length(); i++) {
            String character = bulletin.getDescription().getTitle().substring(i, i + 1);
            if (character.matches("[a-zA-Z0-9]")) {
                result.append(character);
            }
        }
        return result.toString().toLowerCase();
    }

    static Date toDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        try {
            return new SimpleDateFormat("dd MMMM yyyy").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Date formatting failed, date:" + date);
        }

    }


}
