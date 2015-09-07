package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.builder.FieldFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;

import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchPort;
import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.getElasticSearchServer;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 07/09/15.
 */
public class SearchService {

    private Client client;
    private static SearchService instance = new SearchService();

    private static final String ALL_FIELDS = "_all";


    private SearchService() {
        client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(getElasticSearchServer(), getElasticSearchPort()));

    }

    public static SearchService getInstance() {
        return instance;
    }

    public void search(QueryBuilder queryBuilder) {
        buildFilter(queryBuilder);
    }



    public void count(QueryBuilder queryBuilder) {

    }

    private void buildFilter(QueryBuilder queryBuilder) {
        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
        if (isNotEmpty(queryBuilder.getUriPrefix())) {
            andFilterBuilder.add(FilterBuilders.prefixFilter("uri", queryBuilder.getUriPrefix()));
        }
        if (!queryBuilder.getFieldFilters().isEmpty()) {
            
        }
    }

    private void buildFieldFilters(QueryBuilder queryBuilder) {
        List<FieldFilter> fieldFilters = queryBuilder.getFieldFilters();
        for (FieldFilter fieldFilter : fieldFilters) {
            Filterbuilders.
        }
    }

    private Query buildQuery(QueryBuilder queryBuilder) {
        new MatchQueryBuilder(ALL_FIELDS, queryBuilder.getQuery());
    }

}
