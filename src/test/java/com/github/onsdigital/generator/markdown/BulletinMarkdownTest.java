package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.statistic.document.Bulletin;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class BulletinMarkdownTest {

	Bulletin bulletin;

	@Before
	public void setup() {
		bulletin = new Bulletin(null,null,null,null);
	}

	@Test
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
		assertEquals(summary, bulletin.summary);
		assertEquals(headline1, bulletin.headlines[0]);
		assertEquals(headline2, bulletin.headlines[1]);
		assertEquals(headline3, bulletin.headlines[2]);
		assertEquals(contactName, bulletin.contact.name);
		assertEquals(contactEmail, bulletin.contact.email);
		assertEquals(releaseDate, bulletin.releaseDate);

		// Title
		assertEquals("Analysis of consumer price inflation", bulletin.title);

		// Sections
		assertEquals(3, bulletin.sections.size());
		assertEquals("Bulletin summary", bulletin.sections.get(0).title);
		assertEquals("Summarise bulletin.\n", bulletin.sections.get(0).markdown);
		assertEquals("Section one", bulletin.sections.get(1).title);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, bulletin.sections.get(1).markdown);
		assertEquals("Section two", bulletin.sections.get(2).title);
		String markdown2 = "Lorem ipsum bulletin\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, bulletin.sections.get(2).markdown);

		// Accordion
		assertEquals(1, bulletin.accordion.size());
		assertEquals("Footnotes", bulletin.accordion.get(0).title);
		assertEquals("Bulletin footer", bulletin.accordion.get(0).markdown);
	}

}
