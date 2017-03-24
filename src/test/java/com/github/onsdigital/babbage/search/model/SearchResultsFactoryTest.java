package com.github.onsdigital.babbage.search.model;

import com.github.onsdigital.babbage.search.input.SortBy;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.search.model.QueryType.COUNTS;
import static com.github.onsdigital.babbage.search.model.QueryType.DEPARTMENTS;
import static com.github.onsdigital.babbage.search.model.QueryType.FEATURED;
import static com.github.onsdigital.babbage.search.model.QueryType.SEARCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by guidof on 21/03/17.
 */
public class SearchResultsFactoryTest {
    private static final List<QueryType> BASE_QUERIES = Lists.newArrayList(SEARCH, COUNTS, FEATURED, DEPARTMENTS);
    private SearchResults searchResults;

    @Before
    public void init() throws IOException {
        final File file = new File(
                "src/test/resources/com/github/onsdigital/babbage/search/model/SearchResultsFactorySample.json");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        searchResults = SearchResultsFactory.getInstance(bytes, SortBy.relevance, 1, 10, BASE_QUERIES);
    }

    @Test
    public void testParseFeaturedResponse() throws IOException {
        final SearchResult result = getSearchResult(FEATURED);


        assertNotNull(result);
        assertEquals(31, result.getTook());
        assertEquals((Long) 3L, result.getNumberOfResults());
        final List<Map<String, Object>> results = result.getResults();
        assertEquals(1, results.size());
        assertEquals("/economy/governmentpublicsectorandtaxes/taxesandrevenue", results
                .get(0)
                .get("uri"));

    }

    @Test
    public void testParseDepartmentResponse() throws IOException {
        final SearchResult result = getSearchResult(DEPARTMENTS);

        assertNotNull(result);
        assertEquals(26, result.getTook());
        assertEquals((Long) 2L, result.getNumberOfResults());
        final List<Map<String, Object>> results = result.getResults();
        assertEquals(1, results.size());
        assertEquals(
                "https://www.gov.uk/government/statistics?keywords=&topics%5B%5D=all&departments%5B%5D=hm-revenue-customs",
                results
                        .get(0)
                        .get("url"));
    }

    @Test
    public void testParseResultResponse() throws IOException {
        final SearchResult result = getSearchResult(SEARCH);
        assertNotNull(result);
        assertEquals(64, result.getTook());
        assertEquals((Long) 1555L, result.getNumberOfResults());
        final List<Map<String, Object>> results = result.getResults();
        assertEquals(10, results.size());
        assertEquals("/economy/grossdomesticproductgdp/timeseries/rndl/ukea", results
                .get(0)
                .get("uri"));
        assertEquals("relevance", result.getSortBy());
    }

    @Test
    public void testParseResultAggregationsResponse() throws IOException {
        final SearchResult result = getSearchResult(SEARCH);
        assertNotNull(result);
        final Map<String, Long> docCounts = result.getDocCounts();
        assertNotNull(docCounts);
        assertEquals("expected to have content in docCounts", 10, docCounts.size());
        assertEquals((Long) 609L, docCounts.get("timeseries"));
        assertEquals((Long) 38L, docCounts.get("static_methodology"));
    }

    @Test
    public void testParseFeaturedAggregationsResponse() throws IOException {
        final SearchResult result = getSearchResult(FEATURED);
        assertNotNull(result);
        final Map<String, Long> docCounts = result.getDocCounts();
        assertNotNull(docCounts);
        assertEquals("expected to have no content in docCounts", 0, docCounts.size());
    }

    @Test
    public void testParseDepartmentAggregationsResponse() throws IOException {
        final SearchResult result = getSearchResult(DEPARTMENTS);
        assertNotNull(result);
        final Map<String, Long> docCounts = result.getDocCounts();
        assertNotNull(docCounts);
        assertEquals("expected to have no content in docCounts", 0, docCounts.size());
    }

    private SearchResult getSearchResult(final QueryType type) {
        return searchResults.getResults()
                            .stream()
                            .filter(sr -> type.equals(sr.getQueryType()))
                            .findFirst()
                            .get();
    }


    @Test
    public void testParseResultSuggestResponse() throws IOException {
        final SearchResult result = getSearchResult(SEARCH);

        assertNotNull(result);
        final List<String> suggestions = result.getSuggestions();
        assertEquals(5, suggestions.size());
        assertEquals("trade", suggestions.get(0));
        assertEquals("taxes", suggestions.get(2));

    }

    @Test
    public void testParseFeaturedSuggestResponse() throws IOException {
        final SearchResult result = getSearchResult(FEATURED);

        assertNotNull(result);
        final List<String> suggestions = result.getSuggestions();
        assertEquals(0, suggestions.size());
    }

    @Test
    public void testParseDepartmentSuggestResponse() throws IOException {
        final SearchResult result = getSearchResult(DEPARTMENTS);

        assertNotNull(result);
        final List<String> suggestions = result.getSuggestions();
        assertEquals(0, suggestions.size());
    }
}