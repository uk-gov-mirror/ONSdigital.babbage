package com.github.onsdigital.search.bean;

import com.github.onsdigital.content.link.PageReference;
import com.github.onsdigital.content.partial.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Represents results aggregated together to be serialized into JSON
 * 
 * @author brn
 *
 */
public class AggregatedSearchResult {

	// Search result of home type pages
	public SearchResult taxonomySearchResult;
	public SearchResult statisticsSearchResult;
	public long timeseriesCount;
	private boolean suggestionBasedResult;
	private String suggestion;

	public boolean isSuggestionBasedResult() {
		return suggestionBasedResult;
	}

	public void setSuggestionBasedResult(boolean suggestionBasedResult) {
		this.suggestionBasedResult = suggestionBasedResult;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public long getNumberOfResults() {
		long numberOfResults = statisticsSearchResult.getNumberOfResults();
		if(taxonomySearchResult != null) {
			return numberOfResults += taxonomySearchResult.getNumberOfResults();
		}
		return numberOfResults;
		 
	}

	public List<PageReference> getAllResults() {
		List<PageReference> results = new ArrayList<>();
		if(taxonomySearchResult != null) {
			results.addAll(taxonomySearchResult.getResults());
		}
		results.addAll(statisticsSearchResult.getResults());
		return results;
	}

}
