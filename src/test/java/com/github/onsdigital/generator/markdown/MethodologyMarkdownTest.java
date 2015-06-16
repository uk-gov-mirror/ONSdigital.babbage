package com.github.onsdigital.generator.markdown;

import com.github.onsdigital.content.page.methodology.Methodology;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class MethodologyMarkdownTest {

	Methodology methodology;

	@Before
	public void setup() {
		methodology = new Methodology();
	}

	@Test
	public void shouldReadBulletin() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String lede = "man";
		String more = "nation";
		ClassLoader classLoader = MethodologyMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/methodology.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		methodology = MethodologyMarkdown.readMethodology(path);

		// Then

		// Header block
		assertEquals(theme, methodology.theme);
		assertEquals(level2, methodology.level2);
		assertEquals(level3, methodology.level3);
		assertEquals(lede, methodology.getDescription().getSummary());

		// Title
		assertEquals("How do we work out the numbers?", methodology.getDescription().getTitle());

		// Sections
		assertEquals(3, methodology.getSections().size());
		assertEquals("Methodology summary", methodology.getSections().get(0).getTitle());
		assertEquals("Summarise methodology.\n", methodology.getSections().get(0).getMarkdown());
		assertEquals("Section one", methodology.getSections().get(1).getTitle());
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, methodology.getSections().get(1).getMarkdown());
		assertEquals("Section two", methodology.getSections().get(2).getTitle());
		String markdown2 = "Lorem ipsum methodology\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, methodology.getSections().get(2).getMarkdown());

		// Accordion
		assertEquals(1, methodology.getAccordion().size());
		assertEquals("Footnotes", methodology.getAccordion().get(0).getTitle());
		assertEquals("Methodology footer", methodology.getAccordion().get(0).getMarkdown());
	}

}
