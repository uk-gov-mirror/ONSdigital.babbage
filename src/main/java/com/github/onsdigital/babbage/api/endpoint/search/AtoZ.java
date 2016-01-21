//package com.github.onsdigital.babbage.api.endpoint.search;
//
//import com.github.davidcarboni.restolino.framework.Api;
//import com.github.onsdigital.babbage.api.util.SearchUtils;
//import com.github.onsdigital.babbage.search.builders.ONSQueryBuilders;
//import com.github.onsdigital.babbage.search.helpers.ONSQuery;
//import com.github.onsdigital.babbage.search.helpers.ONSSearchResponse;
//import com.github.onsdigital.babbage.search.helpers.SearchHelper;
//import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
//import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
//import com.github.onsdigital.babbage.search.input.SortBy;
//import com.github.onsdigital.babbage.search.model.ContentType;
//import com.github.onsdigital.babbage.search.model.SearchResult;
//import com.github.onsdigital.babbage.search.model.field.Field;
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.index.query.QueryBuilder;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.GET;
//import java.util.LinkedHashMap;
//
//import static com.github.onsdigital.babbage.api.util.SearchUtils.buildListQuery;
//import static com.github.onsdigital.babbage.api.util.SearchUtils.buildResponse;
//import static com.github.onsdigital.babbage.api.util.SearchUtils.searchAll;
//import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.combine;
//import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.firstLetterCounts;
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
//import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
//import static org.apache.commons.lang3.StringUtils.isNotEmpty;
//import static org.elasticsearch.index.query.QueryBuilders.termQuery;
//
///**
// * Created by bren on 19/11/15.
// */
//@Api
//public class AtoZ {
//
//    @GET
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String firstLetter = getFirstLetter(request);
//        String searchTerm = extractSearchTerm(request);
//        SortBy sortBy = isNotEmpty(searchTerm) ? SortBy.relevance : SortBy.first_letter;
//
//        LinkedHashMap<String, SearchResult> results = searchAll(queries(firstLetter, query));
//        SearchResult result = results.get("result");
//
//        //If no results were found for a first letter filter search again for all
//        if (result.getNumberOfResults() == 0 && isNotEmpty(firstLetter)) {
//            ONSSearchResponse searchResponse = SearchHelper.search(query);
//            results.put("result", searchResponse.getResult());
//        }
//
//        buildResponse(request, getClass().getSimpleName(), results).apply(request, response);
//    }
//
//    private SearchQueries queries(HttpServletRequest request,  String firstLetter, SortBy sortBy) {
//        return () -> combine(
//                buildListQuery(request, filters()).types(ContentType.bulletin).sortBy(sortBy),
//                firstLetterCounts(query.query()).types(ContentType.bulletin)
//        );
//    }
//
//    private SearchFilter filters() {
//        return (query) -> query.filter(termQuery(Field.latestRelease.fieldName(), true));
//    }
//
//    private String getFirstLetter(HttpServletRequest request) {
//        String prefix = StringUtils.trim(getParam(request, "az"));
//        if (!StringUtils.isEmpty(prefix)) {
//            return prefix.toLowerCase();
//        }
//        return null;
//
//    }
//
//}
