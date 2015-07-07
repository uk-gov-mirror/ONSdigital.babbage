package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.page.base.PageDescription;
import com.github.onsdigital.content.page.statistics.document.article.Article;
import com.github.onsdigital.content.partial.Contact;
import com.github.onsdigital.generator.ContentNode;
import com.github.onsdigital.generator.data.Data;
import org.apache.commons.lang.BooleanUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ArticleMarkdown {

	static final String resourceName = "/articles";

	public static void parse() throws IOException {
		Collection<Path> files = Markdown.getFiles(resourceName);

		for (Path file : files) {

			// Read the article:
			Article article = readArticle(file);

			// Add it to the taxonomy:
			ContentNode folder = Data.getFolder(article.getDescription().theme, article.getDescription().level2, article.getDescription().level3);
			folder.articles.add(article);
		}
	}

	static Article readArticle(Path file) throws IOException {

		// Read the file
		System.out.println("Processing article from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the article
		Article article = new Article();
		PageDescription description = new PageDescription();

		description.setTitle(markdown.title);
		setDescription(description, markdown);
		article.setSections(markdown.sections);
		article.getSections().addAll(markdown.accordion);

        article.setDescription(description);

		return article;
	}

	/**
	 * Reads the "header" information about the article. Information is expected
	 * in the form "key : value" and the header block should be terminated with
	 * an empty line. The recognised keys are as follows.
	 * <ul>
	 * <li>Theme</li>
	 * <li>Level 2</li>
	 * <li>Level 3</li>
	 * <li>Contact title</li>
	 * <li>Contact email</li>
	 * <li>Next release</li>
     * <li>Release date</li>
	 * </ul>
	 * 
	 * @param articleDescription
	 * @param markdown
	 *            The {@link Scanner} to read lines from.
	 */
	private static void setDescription(PageDescription articleDescription, Markdown markdown) {

		Map<String, String> properties = markdown.properties;

		// Location
		articleDescription.theme = org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("theme"), null);
		articleDescription.level2 = org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("level 2"), null);
		articleDescription.level3 = org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("level 3"), null);

		// Additional details
		articleDescription.setSummary(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("summary"), null));

		//Contact info
		Contact contact = new Contact();
		contact.setName(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("contact title"), null));
		contact.setEmail(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("contact email"), null));
		contact.setTelephone(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("phone"), null));
		articleDescription.setContact(contact);

		//TODO: Where is next release?
		Date releaseDate = toDate(properties.remove("release date"));
		articleDescription.setReleaseDate(releaseDate == null ? null : releaseDate);

		// Additional fields for migration:

		articleDescription.setNationalStatistic(BooleanUtils.toBoolean(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("national statistics"), "yes")));
		articleDescription.setLanguage(org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("language"), null));
		// Split keywords by commas:
		String searchKeywordsString = org.apache.commons.lang.StringUtils.defaultIfBlank(properties.remove("search keywords"), "");
		String[] keywords = org.apache.commons.lang.StringUtils.split(searchKeywordsString, ',');
		List<String> searchKeywords = new ArrayList<String>();
		if (keywords != null) {
			for (int i = 0; i < keywords.length; i++) {
				searchKeywords.add(org.apache.commons.lang.StringUtils.trim(keywords[i]));
			}
		}
		articleDescription.setKeywords(searchKeywords);

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Bulletin key not recognised: '" + property + "' (length " + property.length() + " for value '" + properties.get(property) + "')");
		}

	}


	static Date toDate(String date)  {
		if (org.apache.commons.lang.StringUtils.isBlank(date)) {
			return null;
		}

		try {
			return new SimpleDateFormat("dd MMMM yyyy").parse(date);
		} catch (ParseException e) {
			throw new RuntimeException("Date formatting failed, date:" + date);
		}

	}


    /**
     * Sanitises an article title to <code>[a-zA-Z0-9]</code>.
     *
     * @return A sanitised string.
     */
    public static String toFilename(Article article) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < article.getDescription().getTitle().length(); i++) {
            String character = article.getDescription().getTitle().substring(i, i + 1);
            if (character.matches("[a-zA-Z0-9]")) {
                result.append(character);
            }
        }
        return result.toString().toLowerCase();
    }
}
