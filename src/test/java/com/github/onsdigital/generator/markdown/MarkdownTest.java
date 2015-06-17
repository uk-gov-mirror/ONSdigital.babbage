package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.onsdigital.content.partial.markdown.MarkdownSection;
import org.junit.Test;

public class MarkdownTest {

	@Test
	public void shouldExtractTitle() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = "# \t" + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldExtractTitleWithWeirdContent() {

		// Given
		String title = "Title titel titre # titolo";
		String markdown = "# \t" + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldNotExtractTitleIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNull(result);
	}

	@Test
	public void shouldNotExtractTitleWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNull(result);
	}

	@Test
	public void shouldExtractHeading() {

		// Given
		String heading = "Head thing";
		String markdown = "## \t" + heading;

		// When
		MarkdownSection section = Markdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(heading, section.getTitle());
	}

	@Test
	public void shouldExtractHeadingWithWeirdContent() {

		// Given
		String title = "This will make your head ## thing # spin";
		String markdown = "## \t" + title;

		// When
		MarkdownSection section = Markdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(title, section.getTitle());
	}

	@Test
	public void shouldNotExtractHeadingIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		MarkdownSection section = Markdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldNotExtractSectionWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " ## " + title;

		// When
		MarkdownSection section = Markdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldReadProperty() {

		// Given
		String name = "title";
		String value = "value";
		String line = name + ":" + value;

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
		assertEquals(name, property[0]);
		assertEquals(value, property[1]);
	}

	@Test
	public void shouldReadPropertyWithSpaces() {

		// Given
		String name = "title";
		String value = "value";
		String line = "   " + name + " :  " + value + "\t";

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
		assertEquals(name, property[0]);
		assertEquals(value, property[1]);
	}

	@Test
	public void shouldTwoElementArray() {

		// Given
		String line = "";

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
	}
}
