package com.github.onsdigital.babbage.search.builders;

import org.elasticsearch.index.query.QueryBuilder;

import static com.github.onsdigital.babbage.search.model.field.Field.*;
import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.PHRASE;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.disMaxQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * Create a Query Only builder and allow the peripheral to be setup in the ONSQueryBuilders
 */
class ONSCoreQueryBuilder {
    private ONSCoreQueryBuilder() {
        //DO NOT INSTANTIATE THIS CLASS
    }

    static QueryBuilder buildQuery(final String searchTerm) {
        return disMaxQuery()
                .add(boolQuery()
                             .should(matchQuery(title_no_dates.fieldName(),
                                                searchTerm)
                                             .boost(title_no_dates.boost())
                                             .minimumShouldMatch("1<-2 3<80% 5<60%"))
                             .should(matchQuery(title_no_stem.fieldName(),
                                                searchTerm)
                                             .boost(title_no_stem.boost())
                                             .minimumShouldMatch("1<-2 3<80% 5<60%"))
                             .should(multiMatchQuery(searchTerm,
                                                     title.fieldNameBoosted(),
                                                     edition.fieldNameBoosted(),
                                                     content.fieldNameBoosted())
                                             .type(CROSS_FIELDS)
                                             .minimumShouldMatch("3<80% 5<60%"))
                             .should(multiMatchQuery(searchTerm,
                                                     title.fieldNameBoosted(),
                                                     edition.fieldNameBoosted(),
                                                     content.fieldNameBoosted()).type(PHRASE)
                                                                                .slop(2)
                                                                                .boost(10)))
                .add(multiMatchQuery(searchTerm,
                                     summary.fieldNameBoosted(),
                                     metaDescription.fieldNameBoosted(),
                                     content.fieldNameBoosted())
                             .type(BEST_FIELDS)
                             .minimumShouldMatch("75%"))
                .add(matchQuery(keywords.fieldName(),
                                searchTerm).operator(AND))
                .add(multiMatchQuery(searchTerm,
                                     cdid.fieldNameBoosted(),
                                     datasetId.fieldNameBoosted()))
                .add(matchQuery(searchBoost.fieldName(),
                                searchTerm)
                             .boost(searchBoost.boost())
                             .operator(AND));
    }
}
