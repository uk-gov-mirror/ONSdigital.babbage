package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.onsdigital.content.statistic.document.Article;
import org.junit.Before;
import org.junit.Test;

public class ArticleMarkdownTest {

	Article article;


	//TODO: Get rid of all null initialized fields
	@Before
	public void setup() {
		article = new Article(null,null, null,null);
	}

	@Test
	public void shouldReadArticle() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String nextRelease = "soon";
		String releaseDate = "01/06/2015";
		ClassLoader classLoader = ArticleMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/article.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		article = ArticleMarkdown.readArticle(path);

		// Then

		// Header block
		assertEquals(theme, article.theme);
		assertEquals(level2, article.level2);
		assertEquals(level3, article.level3);
		assertEquals(contactName, article.contact.name);
		assertEquals(contactEmail, article.contact.email);
		assertEquals(nextRelease, article.nextReleaseDate);
		assertEquals(releaseDate, article.releaseDate);

		// Title
		assertEquals("What happened to all the money?", article.name);

		// Sections
		assertEquals(3, article.sections.size());
		assertEquals("Article summary", article.sections.get(0).name);
		assertEquals("Summarise article.\n", article.sections.get(0).markdown);
		assertEquals("Section one", article.sections.get(1).name);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, article.sections.get(1).markdown);
		assertEquals("Section two", article.sections.get(2).name);
		String markdown2 = "Lorem ipsum article\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, article.sections.get(2).markdown);

		// Accordion
		assertEquals(1, article.accordion.size());
		assertEquals("Footnotes", article.accordion.get(0).name);
		assertEquals("Article footer", article.accordion.get(0).markdown);
	}

}
