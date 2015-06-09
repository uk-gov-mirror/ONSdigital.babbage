package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.statistic.document.Article;
import com.github.onsdigital.generator.ContentNode;
import com.github.onsdigital.generator.data.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

public class ArticleMarkdown {

	static final String resourceName = "/articles";

	public static void parse() throws IOException {
		Collection<Path> files = Markdown.getFiles(resourceName);

		for (Path file : files) {

			// Read the article:
			Article article = readArticle(file);

			// Add it to the taxonomy:
			ContentNode folder = Data.getFolder(article.theme, article.level2, article.level3);
			folder.articles.add(article);
		}
	}

	static Article readArticle(Path file) throws IOException {

		// Read the file
		System.out.println("Processing article from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the article
		Article article = new Article();
		article.title = markdown.title;
		article.title = markdown.title;
		setProperties(article, markdown);
		article.sections.addAll(markdown.sections);
		article.accordion.addAll(markdown.accordion);
//		article.fileName = markdown.toFilename();

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
	 * @param article
	 * @param markdown
	 *            The {@link Scanner} to read lines from.
	 */
	private static void setProperties(Article article, Markdown markdown) {

		Map<String, String> properties = markdown.properties;

		// Location
		article.theme = StringUtils.defaultIfBlank(properties.remove("theme"), article.theme);
		article.level2 = StringUtils.defaultIfBlank(properties.remove("level 2"), article.level2);
		article.level3 = StringUtils.defaultIfBlank(properties.remove("level 3"), article.level3);

		// Additional details
		article.contact.name = StringUtils.defaultIfBlank(properties.remove("contact title"), article.theme);
		article.contact.email = StringUtils.defaultIfBlank(properties.remove("contact email"), article.theme);


		//TODO: Where is next release?
		Date releaseDate = toDate(properties.remove("release date"));
		Date nextReleaseDate = toDate(properties.remove("next release"));

		article.releaseDate = releaseDate == null ? article.releaseDate : releaseDate;
		article.nextReleaseDate = nextReleaseDate == null ? article.nextReleaseDate: releaseDate;

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Article key not recognised: " + property + " (for value '" + properties.get(property) + "')");
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
        for (int i = 0; i < article.title.length(); i++) {
            String character = article.title.substring(i, i + 1);
            if (character.matches("[a-zA-Z0-9]")) {
                result.append(character);
            }
        }
        return result.toString().toLowerCase();
    }
}
