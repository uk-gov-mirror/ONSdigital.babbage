package com.github.onsdigital.babbage.features;

import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.mock.MockHttpServletRequest;
import com.github.onsdigital.babbage.search.ElasticSearchClient;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.google.common.collect.Lists;
import cucumber.api.DataTable;
import cucumber.api.java8.En;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * The Steps to test that the results of the Search query contain the correct results on the first page
 * Created by fawkej on 16/12/2016.
 */
public class SearchSteps implements En {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchSteps.class);

    static {
        try {
            //At this point the Elastic Search client may not have been initialised
            ElasticSearchClient.init();
        } catch (IOException e) {
            LOGGER.error("static initializer() : Failed to initialize elastic search client");
        }
    }

    private String queryName;
    private LinkedHashMap<String, SearchResult> searchResults;

    public SearchSteps() {

        When("^a user searches for the term\\(s\\) \"([^\"]*)\"$",
             (String terms) -> {
                 //Using MockHttpServletRequest as an empty of content and working off defaults
                 HttpServletRequest dummyRequest = new MockHttpServletRequest();
                 final ONSQuery query = SearchUtils.buildSearchQuery(dummyRequest,
                                                                     terms,
                                                                     TypeFilter.getDataFilters());
                 //Need to get the name of the query so we can get to the results
                 queryName = query.name();
                 searchResults = SearchUtils.searchAll(() -> Lists.newArrayList(query));

             });

        Then("^the user will receive the following documents on the first page$",
             (DataTable expectedInitialPage) -> {
                 // Write code here that turns the phrase above into concrete actions
                 List<Map<String, Object>> result = searchResults.get(queryName)
                                                                 .getResults();
                 List<String> documentUris = result.stream()
                                                   .map((resultEntry) -> (String) resultEntry.get("uri"))
                                                   .collect(Collectors.toList());

                 for (String expectedUri : expectedInitialPage.asList(String.class)) {

                     assertTrue(
                             String.format("Document URI %s was not in the list returned;\r\n%s",
                                           expectedUri,
                                           StringUtils.join(documentUris,
                                                            "\r\n")),
                             documentUris.contains(expectedUri));
                 }

             });



    }

}
