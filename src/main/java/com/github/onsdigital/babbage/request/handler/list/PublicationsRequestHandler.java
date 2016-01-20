package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.ListUtils;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.ListFilter;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

import static com.github.onsdigital.babbage.search.helpers.SearchHelper.resolveContentTypes;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Render a list page for bulletins under the given URI.
 */
public class PublicationsRequestHandler implements ListRequestHandler {

    private static Set<TypeFilter> publicationFilters = TypeFilter.getPublicationFilters();
    //    private static ContentType[] contentTypesToCount = addAll(resolveContentTypes(publicationFilters), resolveContentTypes(TypeFilter.getDataFilters()));
    private static ContentType[] contentTypesToCount = resolveContentTypes(publicationFilters);

    private final static String REQUEST_TYPE = "publications";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return ListUtils.listPage(request, publicationFilters, contentTypesToCount, REQUEST_TYPE, filters());
    }

    @Override
    public BabbageResponse getData(String data, HttpServletRequest request) throws IOException {
        return ListUtils.listJson(request, publicationFilters, contentTypesToCount, REQUEST_TYPE, filters());
    }

    private ListFilter filters() {
        return (request, listQuery) -> {
            if (request.getParameter("allReleases") == null) {
                listQuery.filter(termQuery(Field.latestRelease.fieldName(), true));
            }
        };
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

}
