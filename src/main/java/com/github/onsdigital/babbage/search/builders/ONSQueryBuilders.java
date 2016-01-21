package com.github.onsdigital.babbage.search.builders;

import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.github.onsdigital.babbage.search.model.field.Field.*;
import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.weightFactorFunction;

/**
 * Created by bren on 20/01/16.
 */
public class ONSQueryBuilders {

    /**
     * Base content query with field boosts. No content type boost applied
     *
     * @param searchTerm
     * @return
     */
    public static QueryBuilder contentQuery(String searchTerm, SearchFilter filter) {
        QueryBuilder query = disMaxQuery()
                .add(boolQuery()
                        .should(matchQuery(title_no_dates.fieldName(), searchTerm)
                                        .boost(title_no_dates.boost())
                                        .minimumShouldMatch("1<-2 3<80% 5<60%")
                        )
                        .should(multiMatchQuery(searchTerm, title.fieldNameBoosted(), edition.fieldNameBoosted())
                                .type(CROSS_FIELDS).minimumShouldMatch("3<80% 5<60%")))
                .add(multiMatchQuery(searchTerm, summary.fieldNameBoosted(), metaDescription.fieldNameBoosted())
                        .type(BEST_FIELDS).minimumShouldMatch("75%"))
                .add(matchQuery(keywords.fieldNameBoosted(), searchTerm).operator(AND))
                .add(multiMatchQuery(searchTerm, cdid.fieldNameBoosted(), datasetId.fieldNameBoosted()).operator(AND));

        if (filter != null) {
            query = appyFilter(QueryBuilders.boolQuery().must(query), filter);
        }

        return query;
    }

    public static QueryBuilder contentQuery(String searchTerm) {
        return contentQuery(searchTerm, null);

    }


    /**
     * Boosts content types based on content type weights defined in {@link ContentType}
     *
     * @param queryBuilder
     * @return
     */
    public static QueryBuilder typeBoostedQuery(QueryBuilder queryBuilder) {
        FunctionScoreQueryBuilder builder = functionScoreQuery(queryBuilder);
        for (ContentType contentType : ContentType.values()) {
            if (contentType.getWeight() != null) {
                builder.add(termQuery(_type.fieldName(), contentType.name()), weightFactorFunction(contentType.getWeight()));
            }
        }
        return builder;
    }

    public static ONSQuery bestTopicMatchQuery(String searchTerm) {
        return onsQuery(contentQuery(searchTerm))
                .types(ContentType.product_page)
                .size(1);
    }

    /**
     * Counts documents based on _type field.
     * <p>
     * Applies documents counts aggregation to given query. Counts will be aggregated within the matching documents list.
     *
     * @return
     */
    public static ONSQuery docCountsQuery(QueryBuilder queryBuilder) {
        return docCountsQuery(queryBuilder, null);
    }

    public static ONSQuery docCountsQuery(QueryBuilder queryBuilder, SearchFilter filter) {
        return onsQuery(queryBuilder, filter)
                .size(0).aggregate(AggregationBuilders.terms("docCounts")
                        .field(Field._type.name())); //aggregating all content types without using selected numbers
    }

    public static ONSQuery onsQuery(QueryBuilder queryBuilder) {
        return onsQuery(queryBuilder, null);
    }

    public static ONSQuery onsQuery(QueryBuilder queryBuilder, SearchFilter filter) {
        if (filter != null) {
            queryBuilder = appyFilter(QueryBuilders.boolQuery().must(queryBuilder), filter);
        }
        return new ONSQuery(queryBuilder);
    }


    public static List<ONSQuery> combine(ONSQuery... queries) {
        ArrayList<ONSQuery> list = new ArrayList<>();
        for (ONSQuery query : queries) {
            list.add(query);
        }
        return list;
    }


    private static QueryBuilder appyFilter(BoolQueryBuilder queryBuilder, SearchFilter filter) {
        filter.filter(queryBuilder);
        return queryBuilder;
    }


}
