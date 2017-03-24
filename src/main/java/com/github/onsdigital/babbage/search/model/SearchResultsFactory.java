package com.github.onsdigital.babbage.search.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.input.SortBy;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.Configuration.GENERAL.getMaxVisiblePaginatorLink;
import static com.github.onsdigital.babbage.util.ListUtil.nullSafeForEach;
import static com.github.onsdigital.babbage.util.ListUtil.nullSafeGet;
import static com.github.onsdigital.babbage.util.MapUtil.nullSafeForEach;
import static com.github.onsdigital.babbage.util.MapUtil.nullSafeGet;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

/**
 * Created by guidof on 20/03/17.
 */
@SuppressWarnings("unchecked")
public class SearchResultsFactory {

    private static final String SEARCH_SUGGEST = "search_suggest";
    private static final String TOTAL = "total";
    private static final String HITS = "hits";
    private static final String TOOK = "took";
    private static final String AGGREGATIONS = "aggregations";
    private static final String DOC_COUNTS = "docCounts";
    private static final String BUCKETS = "buckets";
    private static final String KEY = "key";
    private static final String DOC_COUNT = "doc_count";
    private static final ObjectMapper MAPPER = new ObjectMapper();


    private SearchResultsFactory() {
        //FACTORY SHOULD NOT BE INSTANTIATED
    }

    public static SearchResults getInstance(byte[] searchResponse,
                                            final SortBy sortBy,
                                            final long page,
                                            final int size,
                                            List<QueryType> queries) throws IOException {

        final List<Map> responses = nullSafeGet(MAPPER.readValue(searchResponse, Map.class),
                                                "responses");

        final SearchResults results = new SearchResults();

        //Sort into return order so we can pick them up in the correct order
        queries.sort(QueryType.positionComparator());

        if (null != responses) {
            int idx = 0;
            for (QueryType queryType : queries) {
                if (idx < responses.size()) {
                    final Map data = nullSafeGet(responses, idx);

                    if (null != data) {
                        SearchResult result = new SearchResult();
                        result.setQueryType(queryType);
                        results.addResult(queryType, result);
                        result.setTook(extractLong(data, TOOK));

                        extractHits(data, result);
                        extractAggregations(data, result);
                        extractSuggestions(data, result);

                        if (QueryType.SEARCH.equals(queryType)) {
                            result.setSortBy(sortBy.name());
                            final Long numberOfResults = result.getNumberOfResults();
                            Paginator paginator = new Paginator((null != numberOfResults ? numberOfResults : 0),
                                                                getMaxVisiblePaginatorLink(),
                                                                page,
                                                                size);
                            //Not sure why this is done here, should be done while rendering not while building the model
                            if (paginator.getNumberOfPages() > 1) {
                                result.setPaginator(paginator);
                            }
                        }
                    }
                }
                idx++;
            }
        }

        return results;
    }


    private static void extractSuggestions(final Map data, final SearchResult result) {
        final Map<String, List<Map>> suggest = nullSafeGet((Map<String, Map<String, List<Map>>>) data,
                                                           "suggest");

        final List<Map> searchSuggestions = nullSafeGet(suggest, SEARCH_SUGGEST);
        final Map searchSuggestion = nullSafeGet(searchSuggestions, 0);
        final List<Map> options = nullSafeGet((Map<String, List<Map>>) searchSuggestion,
                                              "options");
        nullSafeForEach(options,
                        map -> result.getSuggestions()
                                     .add(extractString(map, "text")));


    }


    private static void extractAggregations(final Map data, final SearchResult result) {
        final Map aggs = nullSafeGet((Map<String, Map>) data, AGGREGATIONS);
        final Map docCounts = nullSafeGet((Map<String, Map>) aggs, DOC_COUNTS);
        final List<Map<String, Object>> buckets = nullSafeGet((Map<String, List>) docCounts, BUCKETS);
        nullSafeForEach(buckets, (map -> result.getDocCounts()
                                               .put(extractString(map, KEY),
                                                    extractLong(map, DOC_COUNT))));

    }

    private static void extractHits(final Map data, final SearchResult result) {
        Map hits = (Map) nullSafeGet(data, HITS);
        result.setNumberOfResults(extractLong(hits, TOTAL));
        final List<Map<String, Map<String, Object>>> hitList
                = (List<Map<String, Map<String, Object>>>) nullSafeGet(hits, HITS);
        nullSafeForEach(hitList, row -> buildResultSet(result, row));

    }

    private static void buildResultSet(final SearchResult result, final Map<String, Map<String, Object>> row) {
        final Map<String, Object> source = nullSafeGet(row, "_source");

        //override with Highlights
        final Map highlights = nullSafeGet(row, "highlight");

        nullSafeForEach((Map<String, List<String>>) highlights,
                        entry -> source.put(entry.getKey(),
                                            StringUtils.join(entry.getValue(), "…")));

        if (null != source) {
            result.getResults()
                  .add(source);
        }
    }

    private static String extractString(final Map data, final String key) {
        return (String) nullSafeGet(data, key);
    }

    private static Long extractLong(final Map hits, final String key) {
        Long returnVal = null;
        final Object o = nullSafeGet(hits, key);

        if (o instanceof Number) {
            returnVal = ((Number) o).longValue();
        }
        else if (o instanceof String && isNumber((String) o)) {
            returnVal = toLong((String) o);
        }
        return returnVal;
    }
}
