package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Article;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
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
			Folder folder = Data.getFolder(article.theme, article.level2, article.level3);
			folder.articles.add(article);
		}
	}

	static Article readArticle(Path file) throws IOException {

		// Read the file
		System.out.println("Processing article from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the article
		Article article = new Article();
		article.name = markdown.title;
		article.title = markdown.title;
		setProperties(article, markdown);
		article.sections.addAll(markdown.sections);
		article.accordion.addAll(markdown.accordion);
		article.fileName = markdown.toFilename();

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
	 * <li>Contact name</li>
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
		article.contact.name = StringUtils.defaultIfBlank(properties.remove("contact name"), article.theme);
		article.contact.email = StringUtils.defaultIfBlank(properties.remove("contact email"), article.theme);
		article.nextRelease = StringUtils.defaultIfBlank(properties.remove("next release"), article.theme);
		article.releaseDate = StringUtils.defaultIfBlank(properties.remove("release date"), article.theme);

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Article key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}
}
