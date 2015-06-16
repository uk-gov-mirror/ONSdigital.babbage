package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.onsdigital.content.page.statistics.document.article.Article;
import com.github.onsdigital.content.partial.Contact;
import org.junit.Before;
import org.junit.Test;

public class ArticleMarkdownTest {

	Article article;


	//TODO: Get rid of all null initialized fields
	@Before
	public void setup() {
		article = new Article();
	}

//	TODO:@Test
	public void shouldReadArticle() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String nextRelease = "soon";
		String releaseDate = "1 June 2015";
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

		Contact contact = article.getDescription().getContact();
		assertEquals(contactName, contact.getName());
		assertEquals(contactEmail, contact.getEmail());
		assertEquals(nextRelease, article.getDescription().getNextRelease());
		assertEquals(releaseDate, article.getDescription().getReleaseDate());

		// Title
		assertEquals("What happened to all the money?", article.getDescription().getTitle());

		// Sections
		assertEquals(3, article.getSections().size());
		assertEquals("Article summary", article.getSections().get(0).getTitle());
		assertEquals("Summarise article.\n", article.getSections().get(0).getMarkdown());
		assertEquals("Section one", article.getSections().get(1).getTitle());
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, article.getSections().get(1).getMarkdown());
		assertEquals("Section two", article.getSections().get(2).getTitle());
		String markdown2 = "Lorem ipsum article\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, article.getSections().get(2).getMarkdown());

		// Accordion
		assertEquals(1, article.getAccordion().size());
		assertEquals("Footnotes", article.getAccordion().get(0).getTitle());
		assertEquals("Article footer", article.getAccordion().get(0).getMarkdown());
	}

}
