package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.ListFilter;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.*;
import static com.github.onsdigital.babbage.search.helpers.ONSQueryBuilders.*;
import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 19/01/16.
 */
public class ListUtils {


    public static BabbageResponse listJson(HttpServletRequest request, Set<TypeFilter> defaultFilters, ContentType[] typesToCount, String listType, ListFilter... filters) throws IOException {
        ArrayList<ONSQuery> searchList = list(request, defaultFilters, typesToCount, filters);
        return buildDataResponse(listType, doSearch(searchList));
    }

    public static BabbageResponse listPage(HttpServletRequest request, Set<TypeFilter> defaultFilters, ContentType[] typesToCount, String listType, ListFilter... filters) throws IOException {
        ArrayList<ONSQuery> searchList = list(request, defaultFilters, typesToCount, filters);
        return buildPageResponse(listType, doSearch(searchList));
    }

    static ArrayList<ONSQuery> list(HttpServletRequest request, Set<TypeFilter> defaultFilters, ContentType[] typesToCount, ListFilter... filters) {
        String searchTerm = extractSearchTerm(request);
        ArrayList<ONSQuery> searchList = new ArrayList<>();
        BoolQueryBuilder listQuery = buildBaseQuery(request, searchTerm);
        resolveFilters(request, listQuery, filters);
        searchList.add(buildQuery(request, defaultFilters, searchTerm, listQuery));
        searchList.add(docCountsQuery(listQuery).types(typesToCount).size(0));//type counts
        return searchList;
    }

    private static void resolveFilters(HttpServletRequest request, BoolQueryBuilder listQuery, ListFilter... filters) {
        if (filters == null) {
            return;
        }
        for (ListFilter filter : filters) {
            filter.filter(request, listQuery);
        }
    }

    static ONSQuery buildQuery(HttpServletRequest request, Set<TypeFilter> defaultFilters, String searchTerm, QueryBuilder listQuery) {
        Set<TypeFilter> selectedFilters = extractSelectedFilters(request, defaultFilters);
        ContentType[] selectedContentTypes = resolveContentTypes(selectedFilters);
        int page = extractPage(request);

        SortBy sortBy = extractSortBy(request, isNotEmpty(searchTerm) ? SortBy.relevance : SortBy.release_date);
        return onsQuery(typeBoostedQuery(listQuery))
                .types(selectedContentTypes)
                .page(page)
                .sortBy(sortBy)
                .highlight(true);//content query
    }

    private static BoolQueryBuilder buildBaseQuery(HttpServletRequest request, String searchTerm) {
        BoolQueryBuilder listQuery = QueryBuilders.boolQuery();
        if (isNotEmpty(searchTerm)) {
            listQuery.must(contentQuery(searchTerm));
        }
        return listQuery;
    }


    static LinkedHashMap<String, Object> getBaseListTemplate(String listType) {
        LinkedHashMap<String, Object> baseData = new LinkedHashMap<>();
        baseData.put("type", "list");
        baseData.put("listType", listType.toLowerCase());
        return baseData;
    }

    public static void filterDates(HttpServletRequest request, BoolQueryBuilder listQuery) {
        Date[] dates = SearchRequestHelper.extractPublishDates(request);
        listQuery.filter(QueryBuilders.rangeQuery(Field.releaseDate.fieldName()).from(dates[0]).to(dates[1]));
    }

    public static void filterUriPrefix(HttpServletRequest request, BoolQueryBuilder listQuery) {
        Date[] dates = SearchRequestHelper.extractPublishDates(request);
        listQuery.filter(QueryBuilders.rangeQuery(Field.releaseDate.fieldName()).from(dates[0]).to(dates[1]));
    }

}
