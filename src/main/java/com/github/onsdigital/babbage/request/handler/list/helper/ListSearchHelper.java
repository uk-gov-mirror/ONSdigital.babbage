package com.github.onsdigital.babbage.request.handler.list.helper;

import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
import com.github.onsdigital.babbage.search.query.SortOrder;
import com.github.onsdigital.babbage.search.query.Type;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * Created by bren on 07/09/15.
 */
public class ListSearchHelper {

    public SearchResponseHelper list(String uriPrefix, Set<String> allowedTypes, HttpServletRequest request) throws IOException {
        ONSQueryBuilder onsQueryBuilder = buildQuery(uriPrefix, allowedTypes, request);
        SearchResponseHelper searchResponse = SearchService.getInstance().search(onsQueryBuilder);
        return searchResponse;
    }

    private ONSQueryBuilder buildQuery(String uriPrefix, Set<String> allowedTypes, HttpServletRequest request) {
        Type[] types = extractTypes(allowedTypes, request);
        int page = extractPage(request);
        String sortField = request.getParameter("sortBy");

        ONSQueryBuilder onsQueryBuilder = new ONSQueryBuilder();

        onsQueryBuilder.setTypes(types);
        onsQueryBuilder.setPage(page);
        if (StringUtils.isNotEmpty(uriPrefix)) {
            onsQueryBuilder.setUriPrefix(uriPrefix);
        }
        if (StringUtils.isNotEmpty(sortField)) {
            onsQueryBuilder.addSort(sortField.trim(), SortOrder.DESC);
        }

        return onsQueryBuilder;
    }

    private Type[] extractTypes(Set<String> allowedTypes, HttpServletRequest request) {
        String[] types = request.getParameterValues("type");
        Type[] typesToQuery = new Type[0];
        if (types == null) {
            types = new String[0];
            types = allowedTypes.toArray(types);
        }
        for (String type : types) {
            if (allowedTypes.contains(type)) {
                typesToQuery = ArrayUtils.add(typesToQuery, new Type(type));
            }
        }

        if (typesToQuery.length > 0) {
            return typesToQuery;
        }

        for (String allowedType : allowedTypes) {
            typesToQuery = ArrayUtils.add(typesToQuery, new Type(allowedType));
        }
        return typesToQuery;

    }

    /**
     * Extract the page number from a request - for paged results.
     *
     * @param request
     * @return
     */
    private int extractPage(HttpServletRequest request) {
        String page = request.getParameter("page");

        if (StringUtils.isEmpty(page)) {
            return 1;
        }
        if (StringUtils.isNumeric(page)) {
            int pageNumber = Integer.parseInt(page);
            if (pageNumber < 1) {
                return 1;
            }
            return pageNumber;
        } else {
            return 1;
        }
    }

}
