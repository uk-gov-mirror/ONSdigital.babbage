package com.github.onsdigital.babbage.publishing;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.publishing.model.PublishInfo;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;
import com.github.onsdigital.babbage.util.ElasticSearchUtils;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.search.ElasticSearchClient.getElasticsearchClient;
import static org.apache.commons.lang.StringUtils.removeEnd;

/**
 * Created by bren on 16/12/15.
 * <p/>
 * Handles notifications sent by Florence to manage publish dates, hence the caching
 */
public class PublishingManager {

    private static PublishingManager instance = new PublishingManager();
    private static ElasticSearchUtils searchUtils;
    private static final String PUBLISH_DATES_INDEX = "publish";
    private static final String PUBLISH_DATES_TYPE = "dates";

    private PublishingManager() {
        searchUtils = new ElasticSearchUtils(getElasticsearchClient());
    }

    public void notifyUpcoming(PublishNotification notification) {
        try (BulkProcessor bulkProcessor = createBulkProcessor()) {
            for (String uri : notification.getUriList()) {
                uri = cleanUri(uri);
                IndexRequestBuilder indexRequestBuilder = getElasticsearchClient().prepareIndex(PUBLISH_DATES_INDEX, PUBLISH_DATES_TYPE, notification.getCollectionId() + uri);
                indexRequestBuilder.setSource(JsonUtil.toJson(new PublishInfo(uri, notification.getCollectionId(), notification.getPublishDate())));
                bulkProcessor.add(indexRequestBuilder.request());
            }
        }
    }

    /**
     * Deletes collecttion uris from the index and triggers reindex for the uri
     *
     * @param notification
     */
    public void notifyPublished(PublishNotification notification) {
        deletePublishDates(notification, true);
    }

    public void notifyPublishCancel(PublishNotification notification) {
        deletePublishDates(notification, false);
    }

    private void deletePublishDates(PublishNotification notification, boolean triggerReindex) {
        try (BulkProcessor bulkProcessor = createBulkProcessor()) {
            for (String uri : notification.getUriList()) {
                uri = cleanUri(uri);
                DeleteRequestBuilder deleteRequest = getElasticsearchClient().prepareDelete(PUBLISH_DATES_INDEX, PUBLISH_DATES_TYPE, notification.getCollectionId() + uri);
                bulkProcessor.add(deleteRequest.request());
                if (triggerReindex) {
                    triggerReindex(notification.getKey(), uri);
                }
            }
        }
    }


    private void triggerReindex(String key, String uri) {
        try {
            ContentClient.getInstance().reIndex(key, uri);
        } catch (ContentReadException e) {
            System.err.println("!!!Warning, failed triggering reindex for " + uri);
            e.printStackTrace();
        }
    }

    public PublishInfo getNextPublishInfo(String uri) {
        FilteredQueryBuilder builder = new FilteredQueryBuilder(null, new TermFilterBuilder("uri", uri));
        SearchRequestBuilder searchRequestBuilder = getElasticsearchClient().prepareSearch(PUBLISH_DATES_INDEX).setTypes(PUBLISH_DATES_TYPE);
        searchRequestBuilder.setSize(1);
        searchRequestBuilder.setQuery(builder).addSort(new FieldSortBuilder("publishDate").ignoreUnmapped(true));
        SearchResponse response = searchRequestBuilder.get();
        if (response.getHits().getTotalHits() > 0) {
            Long publishDate = (Long) response.getHits().getAt(0).getSource().get("publishDate");
            String collectionId = (String) response.getHits().getAt(0).getSource().get("collectionId");
            return new PublishInfo(uri, collectionId, publishDate == null ? null : new Date(publishDate));
        }
        return null;
    }

    public void dropPublishDate(PublishInfo info) {
        getElasticsearchClient().prepareDelete(PUBLISH_DATES_INDEX, PUBLISH_DATES_TYPE, info.getCollectionId() + info.getUri()).get();
    }

    public static PublishingManager getInstance() {
        return instance;
    }

    public static void init() throws IOException {
        System.out.println("Initializing Search service");
        if (Configuration.GENERAL.isCacheEnabled()) {
            initPublishDatesIndex();
        }
        System.out.println("Initialized Search service successfully");
    }

    private static void initPublishDatesIndex() throws IOException {
        if (!searchUtils.isIndexAvailable(PUBLISH_DATES_INDEX)) {
            searchUtils.createIndex(PUBLISH_DATES_INDEX, buildPublishDatesIndexSettings());
        }
    }

    private static Settings buildPublishDatesIndexSettings() {
        Map<String, String> settings = new HashMap<>();
        // default analyzer
        settings.put("analysis.analyzer.default_index.tokenizer", "keyword"); //no analyzing
        settings.put("analysis.analyzer.default_index.filter", "lowercase");
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.put(settings);
        return builder.build();
    }

    //Clears data.json and .json at the end of uri
    private static String cleanUri(String uri) {
        return removeEnd(removeEnd(uri, "data.json"), ".json");
    }


    private BulkProcessor createBulkProcessor() {
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                getElasticsearchClient(),
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        System.out.println("Builk Indexing " + request.numberOfActions() + " publish dates");
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        if (response.hasFailures()) {
                            BulkItemResponse[] items = response.getItems();
                            for (BulkItemResponse item : items) {
                                if (item.isFailed()) {
                                    System.err.println("!!!!!!!!Failed processing: [id:" + item.getFailure().getId() + " error:" + item.getFailureMessage() + "]");
                                }
                            }
                        }
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        System.err.println("Failed executing bulk index :" + failure.getMessage());
                        failure.printStackTrace();
                    }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB))
                .setConcurrentRequests(1)
                .build();

        return bulkProcessor;
    }

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new Date()));
    }

}
