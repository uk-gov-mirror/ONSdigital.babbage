package com.github.onsdigital.babbage.search.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.onsdigital.babbage.paginator.Paginator;
import com.github.onsdigital.babbage.search.external.requests.suggest.SpellCheckRequest;
import com.github.onsdigital.babbage.search.external.requests.suggest.models.SpellingCorrection;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.SearchResult;

import java.io.IOException;
import java.util.*;

public class MockedContentResponse extends MockedSearchResponse {

    private static final int took = 51;

    private List<SpellingCorrection> getTestResult() throws IOException {
        SpellingCorrection correction = new SpellingCorrection("rpo", "rpi", 0.5f);
        SpellingCorrection otherCorrection = new SpellingCorrection("cpl", "cpi", 0.5f);

        return new LinkedList<SpellingCorrection>() {{
            add(correction);
            add(otherCorrection);
        }};
    }

    @Override
    public LinkedHashMap<String, Integer> getDocCounts() {
        return new LinkedHashMap<>();
    }

    @Override
    public SearchResult getSearchResult() throws IOException {
        final Map<String, Object> testSearchResponse = new LinkedHashMap<String, Object>() {{
            put("numberOfResults", numberOfResults);
            put("took", took);
            put("results", testResults());
            put("docCounts", getDocCounts());
            put("paginator", testPaginator());
            put("sortBy", SortBy.relevance.name());
        }};

        String testJson = MAPPER.writeValueAsString(testSearchResponse);

        SearchResult result = MAPPER.readValue(testJson, SearchResult.class);

        List<SpellingCorrection> corrections = this.getTestResult();
        String suggestedCorrection = SpellCheckRequest.buildSuggestedCorrection("rpo cpl", corrections, 0.0f);

        result.setSuggestions(Collections.singletonList(suggestedCorrection));
        return result;
    }

    private Paginator testPaginator() {
        Paginator paginator = new Paginator(54, 10, 1, 10);
        return paginator;
    }

    private List<Map<String, Object>> testResults() throws IOException {
        String json = "[\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"summary\": \"Measures of inflation data including CPIH, CPI and <strong>RPI</strong>. These tables complement the Consumer Price Inflation time series datasets available on our website.\",\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"unit\": \"\",\n" +
                "      \"keywords\": [\n" +
                "        \"economy\",\n" +
                "        \"weights\",\n" +
                "        \"index\",\n" +
                "        \"indices\",\n" +
                "        \"retail\",\n" +
                "        \"OOH\",\n" +
                "        \"owner occupiers' housing costs\"\n" +
                "      ],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900 \",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"Consumer price inflation\",\n" +
                "      \"metaDescription\": \"Measures of inflation data including CPIH, CPI and <strong>RPI</strong>. These tables complement the Consumer Price Inflation time series datasets available on our website.\",\n" +
                "      \"nationalStatistic\": false,\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [\n" +
                "      \"rpi\"\n" +
                "    ],\n" +
                "    \"type\": \"dataset_landing_page\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/datasets/consumerpriceinflation\",\n" +
                "    \"_type\": \"dataset_landing_page\",\n" +
                "    \"_score\": 3224.5896\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong> All Items: Percentage change over 12 months: Jan 1987=100\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"3.5\",\n" +
                "      \"cdid\": \"CZBH\",\n" +
                "      \"unit\": \"%\",\n" +
                "      \"keyNote\": \"Not a National Statistic. Change over 12 months  \",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [\n" +
                "      \"rpi\"\n" +
                "    ],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/czbh/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 2866.0195\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong> All Items Index: Jan 1987=100\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"270.6\",\n" +
                "      \"cdid\": \"CHAW\",\n" +
                "      \"unit\": \"Index, base year = 100\",\n" +
                "      \"keyNote\": \"Not a National Statistic. Index, 1987 = 100\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [\n" +
                "      \"rpi\"\n" +
                "    ],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/chaw/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 2866.0195\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Utilities (Jan 1987=100)\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"254.4\",\n" +
                "      \"cdid\": \"CHOH\",\n" +
                "      \"unit\": \"Index, base year = 100\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/choh/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.818318\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Housing (Jan 1987=100)\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"368.6\",\n" +
                "      \"cdid\": \"CHBF\",\n" +
                "      \"unit\": \"Index, base year = 100\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/chbf/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.818318\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Catering (Jan 1987=100)\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"335.0\",\n" +
                "      \"cdid\": \"CHBC\",\n" +
                "      \"unit\": \"Index, base year = 100\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/chbc/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.818318\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Ave price - Cucumber, each\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"58\",\n" +
                "      \"cdid\": \"CZNB\",\n" +
                "      \"unit\": \"Pence\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/cznb/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.3014007\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Ave price - Grapefruit, each\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"53\",\n" +
                "      \"cdid\": \"DOHN\",\n" +
                "      \"unit\": \"Pence\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/dohn/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.3014007\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: Ave price - Cauliflower, each\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"89\",\n" +
                "      \"cdid\": \"CZNG\",\n" +
                "      \"unit\": \"Pence\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/czng/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 5.3014007\n" +
                "  },\n" +
                "  {\n" +
                "    \"description\": {\n" +
                "      \"nextRelease\": \"13 Jun 2017\",\n" +
                "      \"date\": \"2017 APR\",\n" +
                "      \"keywords\": [],\n" +
                "      \"releaseDate\": \"2017-05-15T23:00:00.000Z\",\n" +
                "      \"source\": \"\",\n" +
                "      \"title\": \"<strong>RPI</strong>: food: beef (Jan 1987=100)\",\n" +
                "      \"sampleSize\": \"0\",\n" +
                "      \"datasetUri\": \"/economy/inflationandpriceindices/datasets/consumerpriceindices\",\n" +
                "      \"number\": \"211.0\",\n" +
                "      \"cdid\": \"DOAD\",\n" +
                "      \"unit\": \"Index, base year = 100\",\n" +
                "      \"contact\": {\n" +
                "        \"name\": \"James Tucker\",\n" +
                "        \"telephone\": \"+44 (0)1633 456900\",\n" +
                "        \"email\": \"cpi@ons.gsi.gov.uk\"\n" +
                "      },\n" +
                "      \"datasetId\": \"MM23\",\n" +
                "      \"preUnit\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"type\": \"timeseries\",\n" +
                "    \"uri\": \"/economy/inflationandpriceindices/timeseries/doad/mm23\",\n" +
                "    \"_type\": \"timeseries\",\n" +
                "    \"_score\": 4.3396955\n" +
                "  }\n" +
                "]";

        List<Map<String, Object>> results = MAPPER.readValue(json, new TypeReference<ArrayList<Map<String, Object>>>() {
        });

        return results;
    }
}
