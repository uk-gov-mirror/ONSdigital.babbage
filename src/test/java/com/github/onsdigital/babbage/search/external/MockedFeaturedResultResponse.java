package com.github.onsdigital.babbage.search.external;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockedFeaturedResultResponse extends MockedContentResponse {

    private List<Map<String, Object>> testResults() throws IOException {
        String json = "[\n" +
                "  {\n" +
                "    \"uri\": \"/economy/inflationandpriceindices\",\n" +
                "    \"type\": \"product_page\",\n" +
                "    \"description\": {\n" +
                "      \"title\": \"Inflation and price indices\",\n" +
                "      \"summary\": \"The rate of increase in prices for goods and services. Measures of inflation and prices include consumer price inflation, producer price inflation, the house price index, index of private housing rental prices, and construction output price indices. \",\n" +
                "      \"keywords\": [\n" +
                "        \"Consumer Price Index,<strong>Retail Price Index</strong>,Producer Price Index,Services Producer Price Indices,Index of Private Housing Rental Prices,CPIH,RPIJ\"\n" +
                "      ],\n" +
                "      \"metaDescription\": \"The rate of increase in prices for goods and services. Measures of inflation and prices include consumer price inflation, producer price inflation, the house price index, index of private housing rental prices, and construction output price indices. \",\n" +
                "      \"unit\": \"\",\n" +
                "      \"preUnit\": \"\",\n" +
                "      \"source\": \"\"\n" +
                "    },\n" +
                "    \"searchBoost\": [],\n" +
                "    \"embedding_vector\": \"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\",\n" +
                "    \"_type\": \"product_page\",\n" +
                "    \"_score\": 0.021380631\n" +
                "  }\n" +
                "]";

        List<Map<String, Object>> results = MAPPER.readValue(json, new TypeReference<ArrayList<Map<String, Object>>>() {
        });

        return results;
    }

}
