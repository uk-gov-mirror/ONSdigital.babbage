package com.github.onsdigital.babbage.search.builders;

import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.BoolQueryBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by bren on 20/01/16.
 */
public class ONSFilterBuilders {

    public static void filterDates(HttpServletRequest request, BoolQueryBuilder listQuery) {
        Date[] dates = SearchRequestHelper.extractPublishDates(request);
        listQuery.filter(rangeQuery(Field.releaseDate.fieldName()).from(dates[0]).to(dates[1]));
    }

    public static void filterUriPrefix(String uri, BoolQueryBuilder listQuery) {
        uri = endsWith(uri, "/") ? uri : uri + "/";
        listQuery.filter(prefixQuery(Field.uri.fieldName(), uri));
    }

    public static void filterLatest(HttpServletRequest request, BoolQueryBuilder listQuery) {
        if (request.getParameter("allReleases") == null) {
            listQuery.filter(termQuery(Field.latestRelease.fieldName(), true));
        }
    }
}
