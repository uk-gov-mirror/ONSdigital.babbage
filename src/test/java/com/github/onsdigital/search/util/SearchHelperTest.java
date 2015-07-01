package com.github.onsdigital.search.util;

import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.search.bean.AggregatedSearchResult;
import org.elasticsearch.ElasticsearchException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class SearchHelperTest {

	@Before
	public void startEmbeddedServer() throws ElasticsearchException,
			IOException {
		ElasticSearchServer.startEmbeddedServer();
		prepareMockData();
	}

	private void prepareMockData() throws ElasticsearchException, IOException {
		ElasticSearchServer
				.getClient()
				.prepareIndex("testindex", "testtype", String.valueOf(1))
				.setSource(
						jsonBuilder().startObject().field("title", "testTitle")
								.field("tags", "taggy", "tennis", "doh")
								.field("theme", "testTheme").endObject()).get();
	}

	@Test
	public void testSearchQuery() throws Exception {
		SearchHelper util = new SearchHelper();
		AggregatedSearchResult result = util.search(new ONSQueryBuilder("testindex")
				.setSearchTerm("do").setFields("tags").getSearchTerm(),1 , "testtype");
		Assert.assertEquals(1, result.getNumberOfResults());
	}

}
