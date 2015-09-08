package com.github.onsdigital.babbage.request.handler.list.helper;

import com.github.onsdigital.babbage.configuration.Configuration;
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

    public SearchResponseHelper list(String uriPrefix, int page , Set<String> allowedTypes, HttpServletRequest request) throws IOException {
        ONSQueryBuilder onsQueryBuilder = buildQuery(uriPrefix, page, allowedTypes, request);
        SearchResponseHelper searchResponse = SearchService.getInstance().search(onsQueryBuilder);
        return searchResponse;
    }

    private ONSQueryBuilder buildQuery(String uriPrefix, int page, Set<String> allowedTypes, HttpServletRequest request) {
        Type[] types = extractTypes(allowedTypes, request);
        String sortField = request.getParameter("sortBy");

        ONSQueryBuilder onsQueryBuilder = new ONSQueryBuilder();

        onsQueryBuilder.setTypes(types);
        onsQueryBuilder.setPage(page);
        onsQueryBuilder.setSize(Configuration.GENERAL.getResultsPerPage());
        if (StringUtils.isNotEmpty(uriPrefix)) {
            onsQueryBuilder.setUriPrefix(uriPrefix);
        }
        if (StringUtils.isNotEmpty(sortField)) {
            onsQueryBuilder.addSort(sortField.trim(), getSortOrder(sortField));
        }

        return onsQueryBuilder;
    }

    private SortOrder getSortOrder(String sortBy) {
        if ("releaseDate".equals(sortBy)) {
            return SortOrder.DESC;
        } else {
            return SortOrder.ASC;
        }
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

}
