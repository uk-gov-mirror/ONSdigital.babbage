//package com.github.onsdigital.babbage.request.handler.list;
//
//import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
//import com.github.onsdigital.babbage.response.base.BabbageResponse;
//import com.github.onsdigital.babbage.search.helpers.ONSQuery;
//import com.github.onsdigital.babbage.search.helpers.SearchHelper;
//import com.github.onsdigital.babbage.search.helpers.SearchRequestHelper;
//import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
//import com.github.onsdigital.babbage.search.input.SortBy;
//import com.github.onsdigital.babbage.search.input.TypeFilter;
//import com.github.onsdigital.babbage.search.model.ContentType;
//import com.github.onsdigital.babbage.search.model.SearchResult;
//import com.github.onsdigital.babbage.search.model.field.Field;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.DisMaxQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Set;
//
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
//import static org.apache.commons.lang.StringUtils.isEmpty;
//import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
//import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
//
///**
// * Render a list page for bulletins under the given URI.
// */
//public class PublicationsRequestHandler implements RequestHandler {
//
//    private static Set<TypeFilter> publicationFilters = TypeFilter.getPublicationFilters();
//    //    private static ContentType[] contentTypesToCount = addAll(resolveContentTypes(publicationFilters), resolveContentTypes(TypeFilter.getDataFilters()));
//    private static ContentType[] contentTypesToCount = resolveContentTypes(publicationFilters);
//
//    private final static String REQUEST_TYPE = "publications";
//
//    @Override
//    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
//        return SearchRequestHelper.list(request, REQUEST_TYPE, () -> {
//            String searchTerm = extractSearchTerm(request);
//            SortBy sortBy = extractSortBy(request);
//            boolean filterLatest = request.getParameter("allReleases") == null;
//
//            QueryBuilder queryBuilder;
//            if (isEmpty(searchTerm)) {
//                queryBuilder = filterLatest ? filterLatest(boolQuery()) : matchAllQuery();
//                sortBy = sortBy == null ? SortBy.release_date : sortBy;
//            } else {
//                DisMaxQueryBuilder baseContentQuery = buildBaseContentQuery(searchTerm);
//                queryBuilder = filterLatest ? filterLatest(boolQuery().should(baseContentQuery)) : baseContentQuery;
//                sortBy = sortBy == null ? SortBy.relevance : sortBy;
//            }
//            ONSQuery docCounts = countDocTypes(queryBuilder, contentTypesToCount);
//
//            queryBuilder = boostContentTypes(queryBuilder);
//
//            ONSQuery contentQuery = onsQuery(request, queryBuilder, publicationFilters);
//            contentQuery.sortBy(sortBy);
//
//            List<SearchResponseHelper> searchResponseHelpers = SearchHelper.searchMultiple(contentQuery, docCounts);
//            LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
//            results.put("result", searchResponseHelpers.get(0).getResult());
//            results.put("counts", searchResponseHelpers.get(1).getResult());
//            return results;
//        });
//    }
//
//
//    private BoolQueryBuilder filterLatest(BoolQueryBuilder queryBuilder) {
//        return queryBuilder.filter(QueryBuilders.termQuery(Field.latestRelease.fieldName(), true));
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//}
