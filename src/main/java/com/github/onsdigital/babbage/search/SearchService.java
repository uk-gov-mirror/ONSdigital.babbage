package com.github.onsdigital.babbage.search;

import com.github.onsdigital.babbage.search.helpers.CountResponseHelper;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.query.Type;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.io.IOException;
import java.util.List;

import static com.github.onsdigital.babbage.configuration.Configuration.ELASTIC_SEARCH.*;

/**
 * Created by bren on 07/09/15.
 */
public class SearchService {

    private Client client;
    private static SearchService instance = new SearchService();

    private SearchService() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", getElasticSearchCluster()).build();

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(getElasticSearchServer(), getElasticSearchPort()));
        Runtime.getRuntime().addShutdownHook(new ShutDownNodeThread(client));
    }

    public static SearchService getInstance() {
        return instance;
    }

    public SearchResponseHelper search(ONSQueryBuilder queryBuilder) throws IOException {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(getElasticSearchIndexAlias()).setQuery(queryBuilder.build());

        if (queryBuilder.getFrom() != null) {
            searchRequestBuilder.setFrom(queryBuilder.getFrom()).setSize(queryBuilder.getSize());
        }
        String[] types = getTypes(queryBuilder);
        if (types != null) {
            searchRequestBuilder.setTypes(types);
        }
        if (queryBuilder.isHighLightFields()) {
            setHighlights(searchRequestBuilder);
        }
        addSorts(queryBuilder.getSorts(), searchRequestBuilder);
        searchRequestBuilder.addSort(new ScoreSortBuilder()); //sort by score last
        System.out.println("Searching with query:\n" + searchRequestBuilder.internalBuilder());
        return new SearchResponseHelper(searchRequestBuilder.get());
    }

    private void setHighlights(SearchRequestBuilder searchRequestBuilder) {
        searchRequestBuilder.addHighlightedField(ONSQueryBuilder.ALL_FIELDS);
        searchRequestBuilder.setHighlighterPreTags(ONSQueryBuilder.HIGHLIGHTER_PRE_TAG);
        searchRequestBuilder.setHighlighterPostTags(ONSQueryBuilder.HIGHLIGHTER_POST_TAG);
        searchRequestBuilder.setHighlighterForceSource(true);
    }

    public CountResponseHelper count(ONSQueryBuilder queryBuilder) {
        CountRequestBuilder countRequestBuilder = client.prepareCount(getElasticSearchIndexAlias()).setQuery(queryBuilder.build());
        String[] types = getTypes(queryBuilder);
        if (types != null) {
            countRequestBuilder.setTypes(types);
        }
        return new CountResponseHelper(countRequestBuilder.get());
    }

    private void addSorts(List<SortBuilder> sorts, SearchRequestBuilder searchRequestBuilder) {
        for (SortBuilder sort : sorts) {
            searchRequestBuilder.addSort(sort);
        }
    }

    private String[] getTypes(ONSQueryBuilder onsQueryBuilder) {
        Type[] types = onsQueryBuilder.getTypes();
        String[] queryTypes = new String[0];
        if (types != null) {
            for (Type type : types) {
                queryTypes = ArrayUtils.add(queryTypes, type.getType());
            }
        }
        if (queryTypes.length > 0) {
            return queryTypes;
        }
        return null;
    }

    private static class ShutDownNodeThread extends Thread {
        private Client client;

        public ShutDownNodeThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            client.close();
        }
    }

}
