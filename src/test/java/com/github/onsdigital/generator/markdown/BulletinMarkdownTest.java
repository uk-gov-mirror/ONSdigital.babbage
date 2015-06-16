package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.page.statistics.document.bulletin.Bulletin;
import org.junit.Before;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class BulletinMarkdownTest {

	Bulletin bulletin;

	@Before
	public void setup() {
		bulletin = new Bulletin();
	}

//	TODO:@Test
	public void shouldReadBulletin() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String summary = "summarizor";
		String headline1 = "Old English revived";
		String headline2 = "leed";
		String headline3 = "leode";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String releaseDate = "soon";
		ClassLoader classLoader = BulletinMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/bulletin.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		bulletin = new BulletinMarkdown().readBulletin(path);

		// Then

		// Header block
		assertEquals(theme, bulletin.theme);
		assertEquals(level2, bulletin.level2);
		assertEquals(level3, bulletin.level3);
		assertEquals(summary, bulletin.getDescription().getSummary());
		assertEquals(headline1, bulletin.getDescription().getHeadline1());
		assertEquals(headline2, bulletin.getDescription().getHeadline2());
		assertEquals(headline3, bulletin.getDescription().getHeadline3());
		assertEquals(contactName, bulletin.getDescription().getContact().getName());
		assertEquals(contactEmail, bulletin.getDescription().getContact().getEmail());
		assertEquals(releaseDate, bulletin.getDescription().getReleaseDate());

		// Title
		assertEquals("Analysis of consumer price inflation", bulletin.getDescription().getTitle());

		// Sections
		assertEquals(3, bulletin.getSections().size());
		assertEquals("Bulletin summary", bulletin.getSections().get(0).getTitle());
		assertEquals("Summarise bulletin.\n", bulletin.getSections().get(0).getMarkdown());
		assertEquals("Section one", bulletin.getSections().get(1).getTitle());
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, bulletin.getSections().get(1).getMarkdown());
		assertEquals("Section two", bulletin.getSections().get(2).getTitle());
		String markdown2 = "Lorem ipsum bulletin\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, bulletin.getSections().get(2).getMarkdown());

		// Accordion
		assertEquals(1, bulletin.getAccordion().size());
//		assertEquals("Footnotes", bulletin.getAccordion().get(0).getTitle());
		assertEquals("Bulletin footer", bulletin.getAccordion().get(0).getMarkdown());
	}

}
