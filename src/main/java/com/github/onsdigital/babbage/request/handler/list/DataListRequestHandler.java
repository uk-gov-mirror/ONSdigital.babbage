//package com.github.onsdigital.babbage.request.handler.list;
//
//import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
//import com.github.onsdigital.babbage.response.base.BabbageResponse;
//import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
//import com.github.onsdigital.babbage.search.input.TypeFilter;
//import com.github.onsdigital.babbage.search.model.ContentType;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.Set;
//
//import static com.github.onsdigital.babbage.api.util.ListUtils.*;
//import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
//
///**
// * Render a list page for bulletins under the given URI.
// */
//public class DataListRequestHandler implements ListRequestHandler {
//
//    private final static String REQUEST_TYPE = "datalist";
//    private static Set<TypeFilter> dataFilters = TypeFilter.getDataFilters();
//    //    private static ContentType[] contentTypesToCount = addAll(resolveContentTypes(dataFilters), resolveContentTypes(TypeFilter.getPublicationFilters()));
//    private static ContentType[] contentTypesToCount = resolveContentTypes(dataFilters);
//
//    @Override
//    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
//        return listPage(request, dataFilters, contentTypesToCount, REQUEST_TYPE, filters(uri));
//    }
//
//    @Override
//    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException {
//        return listJson(request, dataFilters, contentTypesToCount, REQUEST_TYPE, filters(uri));
//    }
//
//    private SearchFilter filters(String uri) {
//        return (request, listQuery) -> {
//            filterUriPrefix(uri, listQuery);
//            filterDates(request, listQuery);
//        };
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//
//}
