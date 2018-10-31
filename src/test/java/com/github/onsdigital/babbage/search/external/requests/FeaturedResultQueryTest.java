package com.github.onsdigital.babbage.search.external.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.search.external.SearchClient;
import com.github.onsdigital.babbage.search.external.requests.mocks.response.MockFeaturedResultResponse;
import com.github.onsdigital.babbage.search.external.requests.search.FeaturedResultQuery;
import com.github.onsdigital.babbage.search.external.requests.search.ListType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FeaturedResultQueryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String searchTerm = "Who ya gonna call?";
    private final ListType listType = ListType.ONS;

    private String expectedResult;

    @Mock
    private SearchClient searchClient;

    private FeaturedResultQuery featuredResultQuery;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        featuredResultQuery = new FeaturedResultQuery(searchTerm, listType);
        TestsUtil.setPrivateField(featuredResultQuery, "searchClient", searchClient);

        CloseableHttpResponse response = new MockFeaturedResultResponse();
        expectedResult = EntityUtils.toString(response.getEntity());

        when(searchClient.execute(featuredResultQuery))
                .thenReturn(response);
    }

    @Test
    public void testSearchResult() throws Exception {
        SearchResult actual = featuredResultQuery.call();
        String actualJson = MAPPER.writeValueAsString(actual);

        assertEquals(actualJson, expectedResult);
    }

}
