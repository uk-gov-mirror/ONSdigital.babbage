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
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Set;
//
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
//import static org.apache.commons.lang.StringUtils.isEmpty;
//import static org.apache.commons.lang3.ArrayUtils.addAll;
//
///**
// * Render a list page for bulletins under the given URI.
// */
//public class DataListRequestHandler implements RequestHandler {
//
//    private final static String REQUEST_TYPE = "datalist";
//    private static Set<TypeFilter> dataFilters = TypeFilter.getDataFilters();
//    //    private static ContentType[] contentTypesToCount = addAll(resolveContentTypes(dataFilters), resolveContentTypes(TypeFilter.getPublicationFilters()));
//    private static ContentType[] contentTypesToCount = resolveContentTypes(dataFilters);
//
//    @Override
//    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
//        return SearchRequestHelper.list(request, REQUEST_TYPE, () -> {
//            String searchTerm = extractSearchTerm(request);
//            boolean hasSearchTerm = StringUtils.isNotEmpty(searchTerm);
//
//            Date[] publishDateRange = SearchRequestHelper.extractPublishDates(request);
//            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
//
//            if (isEmpty(searchTerm)) {
//                queryBuilder = QueryBuilders.matchAllQuery();
//            } else {
//                queryBuilder = buildBaseContentQuery(searchTerm);
//            }
//            queryBuilder = boostContentTypes(queryBuilder);
//            ONSQuery contentQuery = onsQuery(request, queryBuilder, dataFilters);
//            contentQuery.sortBy(sortBy);
//            ONSQuery docCounts = countDocTypes(queryBuilder, contentTypesToCount);
//            List<SearchResponseHelper> searchResponseHelpers = SearchHelper.searchMultiple(contentQuery, docCounts);
//            LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();
//            results.put("result", searchResponseHelpers.get(0).getResult());
//            results.put("counts", searchResponseHelpers.get(1).getResult());
//            return results;
//        });
//    }
//
//    public SortBy resolveSorting(HttpServletRequest request, ONSQuery builder, boolean hasSearchTerm) {
//        SortBy sortBy = extractSortBy(request);
//        if (sortBy == null) {
//            sortBy = hasSearchTerm ? SortBy.relevance : SortBy.release_date;
//        }
//        return sortBy;
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//
//}
