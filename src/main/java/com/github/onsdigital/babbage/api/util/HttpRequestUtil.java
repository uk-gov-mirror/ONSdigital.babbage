package com.github.onsdigital.babbage.api.util;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by guidof on 21/03/17.
 */
public class HttpRequestUtil {

    public static Set<TypeFilter> extractFilters(final String[] filters,
                                                 final Set<TypeFilter> defaultFilters) {
        Set<TypeFilter> typeFilters = new HashSet<>();

        if (ArrayUtils.isNotEmpty(filters)) {
            for (String filter : filters) {
                if (StringUtils.isNotBlank(filter)) {
                    typeFilters.add(TypeFilter.valueOf(filter.toUpperCase()));
                }
            }
        }

        if (typeFilters.size() < 1 && null != defaultFilters) {
            typeFilters.addAll(defaultFilters);
        }
        return typeFilters;
    }

    public static SortBy extractSortBy(HttpServletRequest request, SortBy defaultSort) {
        String sortBy = getParam(request, "sortBy");
        if (isEmpty(sortBy)) {
            return defaultSort;
        }
        try {
            return SortBy.valueOf(sortBy.toLowerCase());
        }
        catch (IllegalArgumentException e) {
            return defaultSort;
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
        }
        catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Extracts search term, checks for parameter "q" if it does not exist looks for parameter "query"
     *
     * @param request
     * @return
     */
    public static String extractSearchTerm(HttpServletRequest request) {
        String query = getParam(request,
                                "q",
                                getParam(request, "query"));//get query if q not given, list pages use query
        if (org.apache.commons.lang3.StringUtils.isEmpty(query)) {
            return null;
        }
        if (query.length() > 200) {
            throw new BadRequestException("Search query contains too many characters");
        }
        return query;
    }
}
