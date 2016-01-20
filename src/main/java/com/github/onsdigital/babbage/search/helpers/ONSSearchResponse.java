package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.model.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.highlight.HighlightField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 07/09/15.
 */
public class ONSSearchResponse {

    SearchResponse response;
    SearchResult result;

    public ONSSearchResponse(SearchResponse response) {
        this.response = response;
        this.result = buildResult();
    }

    public long getNumberOfResults() {
        return response.getHits().getTotalHits();
    }

    public SearchResult getResult() {
        return this.result;
    }

    private SearchResult buildResult() {
        SearchResult searchResult = new SearchResult();
        searchResult.setNumberOfResults(getNumberOfResults());
        searchResult.setTook(response.getTookInMillis());

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> source = extractSource(hit);
            searchResult.addResult(source);
        }

        extractDocCounts(searchResult);

        return searchResult;
    }

    private void extractDocCounts(SearchResult searchResult) {
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            addCounts(searchResult, aggregations);
        }
    }


    /*
    Flattens and adds bucket results recursively to result as key value pairs
     */
    private void addCounts(SearchResult searchResult, Aggregations aggregations) {
        for (Aggregation aggregation : aggregations) {
            if (aggregation instanceof MultiBucketsAggregation) {
                for (MultiBucketsAggregation.Bucket bucket : ((MultiBucketsAggregation) aggregation).getBuckets()) {
                    searchResult.addDocCount(bucket.getKeyAsString(), bucket.getDocCount());
                }
            } else {
                addCounts(searchResult, ((SingleBucketAggregation) aggregation).getAggregations());
            }
        }
    }

    private Map<String, Object> extractSource(SearchHit hit) {
        Map<String, Object> source = new HashMap<>(hit.getSource());
        source.put("_type", hit.getType());
        Map<String, HighlightField> highlightFields = new HashMap<>(hit.getHighlightFields());
        overlayHighlightFields(source, highlightFields);
        return source;
    }


    private void overlayHighlightFields(Map<String, Object> source, Map<String, HighlightField> highlightedFields) {
        if (highlightedFields.isEmpty()) {
            return;
        }

        ArrayList<Map<String, Object>> nestedObjects = new ArrayList<>();
        for (Map.Entry<String, Object> sourceEntry : source.entrySet()) {
            Object field = sourceEntry.getValue();
            if (saveIfNested(nestedObjects, field)) {
                continue;
            } else if (field instanceof Collection) {
                for (Object o : (Collection) field) {
                    saveIfNested(nestedObjects, o);
                }
            }
            HighlightField highlightedField = highlightedFields.remove(sourceEntry.getKey());
            if (highlightedField == null) {//not found
                continue;
            } else {
                overlay(sourceEntry, field, highlightedField);
            }
        }

        for (Map<String, Object> nestedObject : nestedObjects) {
            overlayHighlightFields(nestedObject, highlightedFields);
        }

    }

    private void overlay(Map.Entry<String, Object> sourceEntry, Object field, HighlightField highlightedField) {
        Text[] fragments = highlightedField.getFragments();
        if (field instanceof Collection) {
            Collection arrayField = (Collection) field;
            for (Text fragment : fragments) {
                String fragmentString = fragment.toString();
                String noTag = removeHighlightTags(fragmentString);
                arrayField.remove(noTag);//removes existing non-highlighted text
                arrayField.add(fragmentString);
            }
        } else {
            sourceEntry.setValue(StringUtils.join(fragments, ","));
        }
        return;
    }

    private String removeHighlightTags(String fragment) {
        String s = fragment;
        s = s.replaceAll("<strong>", "");
        s = s.replaceAll("</strong>", "");
        return s;
    }

    private boolean saveIfNested(ArrayList list, Object field) {
        if (field instanceof Map) {
            list.add(field);
            return true;
        }
        return false;
    }

}
