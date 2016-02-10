package com.github.onsdigital.babbage.api.endpoint.rss.service;

import com.github.onsdigital.babbage.api.endpoint.rss.RssSearchFilter;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.LinkedHashMap;
import java.util.Optional;

import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.toList;
import static com.github.onsdigital.babbage.search.model.field.Field.releaseDate;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * Service provides methods to generate the RSS search query and performing the search.
 */
public class RssSearchService {

	private static final String RSS_MAX_FEED_SIZE_KEY = "rss.max.results.size";
	private static final String FEED_DATE_RANGE_KEY = "rss.feed.data.range";
	private static final String RESULTS_KEY = "result";

	private static final RssSearchService INSTANCE = new RssSearchService();

	public static RssSearchService getInstance() {
		return RssSearchService.INSTANCE;
	}

	private RssSearchService() {
		// Hide constructor.
	}

	/**
	 * Generates the elastic search query to find the RSS entries for the parameters provided. The bare minimum required
	 * to generate the query is the topic/uri to search under. If this is not provided an empty optional is returned.
	 *
	 * @param filter
	 * @return
	 */
	public SearchQueries generateQuery(RssSearchFilter filter) {

		SearchFilter searchFilter = (listQuery) -> {
			if (!StringUtils.isEmpty(filter.getUri())) {

				String uri = filter.getUri();
				listQuery.filter(
						boolQuery().should(QueryBuilders.wildcardQuery(Field.topics.fieldName(), uri + "*"))
								.should(prefixQuery(Field.uri.fieldName(), endsWith(uri, "/") ? uri : uri + "/")));
			}

			listQuery.filter(QueryBuilders.rangeQuery(releaseDate.fieldName()).gte(
					PropertiesService.getInstance().get(FEED_DATE_RANGE_KEY))
			);
		};

		ONSQuery onsQuery = SearchUtils.buildListQuery(filter.getRequest(), filter.getTypeFilters(), searchFilter)
				.size(Integer.valueOf(PropertiesService.getInstance().get(RSS_MAX_FEED_SIZE_KEY)))
				.sortBy(SortBy.release_date)
				.highlight(false);

		SearchQueries searchQueries = () -> toList(onsQuery);
		return searchQueries;
	}

	public Optional<SearchResult> search(SearchQueries searchQueries) {
		LinkedHashMap<String, SearchResult> results =  SearchUtils.searchAll(searchQueries);
		return Optional.ofNullable(results.get(RESULTS_KEY));
	}
}
