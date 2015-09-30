package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.search.model.filter.ValueFilter;
import com.github.onsdigital.babbage.util.URIUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 07/09/15.
 */
public class SearchRequestHelper {

    private HttpServletRequest searchRequest;
    private String topicUri;
    private ContentType[] allowedTypes;

    public SearchRequestHelper(HttpServletRequest request, String topicUri, ContentType... allowedTypes) {
        this.searchRequest = request;
        this.topicUri = topicUri;
        this.allowedTypes = allowedTypes;
    }

    public ONSQuery buildQuery() {

        ONSQuery onsQuery = new ONSQuery()
                .setTypes(resolveTypesFilter(allowedTypes))
                .setFields(SearchableField.values())
                .setPage(extractPage(searchRequest))
                .setSize(Configuration.GENERAL.getResultsPerPage())
                .setHighLightFields(true)
                .addRangeFilter(FilterableField.releaseDate, parseDate(getParam(searchRequest,"fromDate")), parseDate(getParam(searchRequest,"toDate")));

        resolveUriPrefix(onsQuery);
        resolveQuery(onsQuery);
        resolveKeywords(onsQuery);
        resolveSorting(onsQuery);
        return onsQuery;
    }

    private void resolveSorting(ONSQuery onsQuery) {
        SortBy sortBy = extractSortBy(searchRequest);
        if (sortBy != null) {
            onsQuery.addSort(sortBy);
        } else {
            onsQuery.addSort(SortBy.RELEVANCE);
        }
    }

    private void resolveUriPrefix(ONSQuery onsQuery) {
        String uriPrefix = URIUtil.cleanUri(topicUri);
        if (isNotEmpty(uriPrefix)) {
            onsQuery.setUriPrefix(cleanUri(uriPrefix));
        }
    }

    private void resolveKeywords(ONSQuery onsQuery) {
        String[] keywords = getParams(searchRequest, "keywords");
        if (keywords != null) {
            for (String keyword : keywords) {
                if (StringUtils.isNotEmpty(keyword)) {
                    onsQuery.addFilter(ValueFilter.FilterType.TERM, FilterableField.keywords_raw, keyword.trim());
                }
            }
        }
    }

    private void resolveQuery(ONSQuery onsQuery) {
        String query = extractSearchTerm(searchRequest);
        if (isNotEmpty(query)) {
            onsQuery.setQuery(query);
        }
    }

    private ContentType[] resolveTypesFilter(ContentType[] allowedTypes) {
        TypeFilter[] requestedFilters = getFiltersByName(getParams(searchRequest, "filter"));
        //if no filter is given use all allowed filters
        if (requestedFilters == null) {
            return allowedTypes;
        }

        //return requested filter requestedTypes if no allowed filter list given
        if (allowedTypes == null) {
            return getTypesFor(requestedFilters);
        }
        return resolveTypeFilter(allowedTypes, requestedFilters);
    }

    private ContentType[] resolveTypeFilter(ContentType[] allowedTypes, TypeFilter[] requestedFilters) {
        Set<ContentType> allowedTypeSet = toSet(allowedTypes);
        ContentType[] typesToQuery = new ContentType[0];
        for (TypeFilter filter : requestedFilters) {
            for (ContentType contentType : filter.getTypes()) {
                if (allowedTypeSet.contains(contentType)) {
                    typesToQuery = ArrayUtils.add(typesToQuery, contentType);
                }
            }

        }

        if (typesToQuery.length > 0) {
            return typesToQuery;
        } else {
            return allowedTypes;
        }
    }

    private TypeFilter[] getFiltersByName(String... filterNames) {
        if (filterNames == null) {
            return null;
        }
        TypeFilter[] filters = null;
        for (String filterName : filterNames) {
            if (isEmpty(filterName)) {
                continue;
            }
            try {
                filters = ArrayUtils.addAll(filters, TypeFilter.valueOf(filterName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                //ignore invalid filter name typed in the url
                continue;
            }
        }

        return filters;
    }

    private ContentType[] getTypesFor(TypeFilter... filters) {
        if (filters == null) {
            return null;
        }
        ContentType[] types = new ContentType[0];
        for (TypeFilter filter : filters) {
            types = ArrayUtils.addAll(types, filter.getTypes());
        }

        return types;
    }

    private Set<ContentType> toSet(ContentType[] types) {
        HashSet<ContentType> typeSet = new HashSet<>();
        for (ContentType type : types) {
            typeSet.add(type);
        }
        return typeSet;
    }

    private Date parseDate(String date) {
        if (isNotEmpty(date)) {
            date = date.trim();
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(date);
            } catch (ParseException e) {
            }
        }
        return null;
    }


    public static String getParam(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    public static String getParam(HttpServletRequest request , String name, String defaultValue) {
        String param = getParam(request, name);
        if (isEmpty(param)) {
            return defaultValue;
        }
        return param;
    }

    public static String[] getParams(HttpServletRequest request, String name) {
        return request.getParameterValues(name);
    }

    public static SortBy extractSortBy(HttpServletRequest request) {
        String sortBy = getParam(request, "sortBy");
        if (isEmpty(sortBy)) {
            return null;
        }
        try {
            return SortBy.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            //ignore invalid sort by parameter
            return null;
        }
    }

    /**
     * Extract the page number from a request - for paged results.
     *
     * @return
     */
    public static int extractPage(HttpServletRequest request) {
        String page = request.getParameter("page");

        if (isEmpty(page)) {
            return 1;
        }
        try {
            int pageNumber = Integer.parseInt(page);
            if (pageNumber < 1) {
                throw new ResourceNotFoundException();
            }
            return pageNumber;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Extracts search term, checks for parameter "q" if it does not exist looks for parameter "query"
     * @param request
     * @return
     */
    public static String extractSearchTerm(HttpServletRequest request) {
        String query = getParam(request, "q", getParam(request, "query"));//get query if q not given, list pages use query
        if (StringUtils.isEmpty(query)) {
            return null;
        }
        if (query.length() > 200) {
            throw new RuntimeException("Search query contains too many characters");
        }
        return query;
//        String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9 ]+", "");
//        return sanitizedQuery;
    }

}
