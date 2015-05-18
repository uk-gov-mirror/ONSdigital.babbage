package com.github.onsdigital.json.markdown;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.DataItem;
import com.github.onsdigital.json.collection.CollectionItem;
import com.github.onsdigital.json.partial.Email;
import com.github.onsdigital.json.taxonomy.TaxonomyHome;

import java.util.ArrayList;
import java.util.List;

public class Bulletin extends CollectionItem {

	public Email contact = new Email();

	// Exec summary
	public String summary;

	// Table of contents
	public List<Section> sections = new ArrayList<>();
	public List<Section> accordion = new ArrayList<>();
	public String headline1;
	public String headline2;
	public String headline3;

	// Additional fields for migration:
	public String phone;
	public String[] searchKeywords;
	public boolean nationalStatistic;
	public String language;

	// Related bulletins - initially this is just other bulletins under the same
	// t3 node:
	public List<DataItem> relatedBulletins = new ArrayList<>();

	// Used to help place bulletins in the taxonomy
	public transient String theme;
	public transient String level2;
	public transient String level3;

	/**
	 * Sets up some basic content.
	 */
	public Bulletin() {
		type = ContentType.bulletin;
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

}
