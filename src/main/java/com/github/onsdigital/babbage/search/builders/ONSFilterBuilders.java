package com.github.onsdigital.babbage.search.builders;

import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.BoolQueryBuilder;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractPublishDates;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.publishedAnyTime;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by bren on 20/01/16.
 */
public class ONSFilterBuilders {

    public static void filterDates(HttpServletRequest request, BoolQueryBuilder listQuery) {
        PublishDates publishDates;
        try {
            publishDates = extractPublishDates(request);
        } catch (PublishDatesException ex) {
            publishDates = publishedAnyTime();
        }
        listQuery.filter(rangeQuery(Field.releaseDate.fieldName())
                .from(publishDates.publishedFrom())
                .to(publishDates.publishedTo()));
    }

    public static void filterUriPrefix(String uri, BoolQueryBuilder listQuery) {
        uri = endsWith(uri, "/") ? uri : uri + "/";
        listQuery.filter(prefixQuery(Field.uri.fieldName(), uri));
    }

    /**
     * Includes documents with uris starting with given uri and has topics containing given uri
     */
    public static void filterUriAndTopics(String uri, BoolQueryBuilder listQuery) {
        listQuery.filter(
                boolQuery().should(termQuery(Field.topics.fieldName(), uri))
                .should(prefixQuery(Field.uri.fieldName(), endsWith(uri, "/") ? uri : uri + "/"))
        );

    }

    public static void filterLatest(HttpServletRequest request, BoolQueryBuilder listQuery) {
        if (request.getParameter("allReleases") == null) {
            listQuery.filter(termQuery(Field.latestRelease.fieldName(), true));
        }
    }

    public static void filterTopic(HttpServletRequest request, BoolQueryBuilder listQuery) {
        String topic = request.getParameter("topic");
        if (isNotEmpty(topic)) {
            filterUriPrefix(topic, listQuery);
        }
    }
}
