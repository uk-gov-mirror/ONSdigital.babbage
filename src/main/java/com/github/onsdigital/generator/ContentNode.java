package com.github.onsdigital.generator;

import com.github.onsdigital.content.page.methodology.Methodology;
import com.github.onsdigital.content.page.statistics.Dataset;
import com.github.onsdigital.content.page.statistics.data.TimeSeries;
import com.github.onsdigital.content.page.statistics.document.Article;
import com.github.onsdigital.content.page.statistics.document.Bulletin;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ContentNode implements Comparable<ContentNode> {

	public int index;
	public String name;
	public ContentNode parent = null;
	private Map<String, ContentNode> children = new HashMap<>();
	public String lede;
	public String more;
	public TimeSeries headline;
	public Bulletin headlineBulletin;
	public Bulletin additonalBulletin;
	public List<TimeSeries> timeserieses = new ArrayList<>();
	public List<Set<TimeSeries>> oldDataset = new ArrayList<Set<TimeSeries>>();

	// Having these as lists preserves the ordering from the spreadsheet.
	public List<Bulletin> bulletins = new ArrayList<>();
	public List<Article> articles = new ArrayList<>();
	public List<Methodology> methodology = new ArrayList<>();

	public Set<Dataset> datasets = new HashSet<>();

	public String filename() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			String character = name.substring(i, i + 1);
			if (character.matches("[a-zA-Z0-9]")) {
				result.append(character);
			}
		}
		return result.toString().toLowerCase();
	}

	public void addChild(ContentNode child) {
		this.children.put(child.filename(), child);
	}

	public void addChildren(Collection<ContentNode> children) {
		for (ContentNode folder : children) {
			this.children.put(folder.filename(), folder);
		}
	}

	public ContentNode getChild(String fileName) {
		return this.children.get(fileName);
	}

	public Collection<ContentNode> getChildren() {
		return children.values();
	}

	public String path() {
		String result = filename();
		ContentNode parent = this;
		while ((parent = parent.parent) != null) {
			result = parent.filename() + "/" + result;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return StringUtils.equals(name, ((ContentNode) obj).name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(ContentNode o) {
		return name.compareTo(o.name);
	}

}
