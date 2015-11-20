package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.ONSQuery;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.field.FilterableField;
import com.github.onsdigital.babbage.search.model.field.SearchableField;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Common utilities to manipulate search request query and extracting common search parameters
 */

public class SearchRequestHelper {


    /**
     * Adds given filters to query as or filters. The or filter will simply be a filter in encapsulating and filter, meaning all other filters added to query separately are anded with each other
     *
     * @param query
     * @param filters
     */
    public static void addOrFilters(ONSQuery query, FilterBuilder... filters) {
        query.addFilter(FilterBuilders.orFilter(filters));
    }

    public static void addFields(ONSQuery query, SearchableField... fields) {
        for (SearchableField field : fields) {
            query.addField(field.name(), field.getBoostFactor());
        }
    }


    /**
     * Adds term filters to ons query
     *
     * @param query
     * @param field
     * @param values
     */
    public static void addPrefixFilter(ONSQuery query, FilterableField field, String... values) {
        if (values == null) {
            query.addFilter(FilterBuilders.prefixFilter(field.name(), null));
        }

        for (String value : values) {
            query.addFilter(FilterBuilders.prefixFilter(field.name(), value));
        }
    }

    /**
     * Adds term filters to ons query
     *
     * @param query
     * @param field
     * @param values
     */
    public static void addTermFilter(ONSQuery query, FilterableField field, Object... values) {
        if (values == null) {
            query.addFilter(FilterBuilders.termFilter(field.name(), null));
        }

        for (Object value : values) {
            query.addFilter(FilterBuilders.termFilter(field.name(), value));
        }
    }

    /**
     * Adds range filter to given query, only if any of from or to values are non-null, null values are not added to filter.
     * If both from and to are null this method won't have any affect
     *
     * @param query
     * @param field
     * @param from
     * @param to
     */

    public static void addRangeFilter(ONSQuery query, FilterableField field, Object from, Object to) {
        if (from == null && to == null) {
            return;
        }

        RangeFilterBuilder dateFilter = new RangeFilterBuilder(field.name());
        if (from != null) {
            dateFilter.from(from);
        }
        if (to != null) {
            dateFilter.to(to);
        }
        query.addFilter(dateFilter);

    }

    public static void addSort(ONSQuery query, SortBy sortBy) {
        SortField[] sortFields = sortBy.getSortFields();
        for (SortField sortField : sortFields) {
            query.addSort(new FieldSortBuilder(sortField.getField().name()).order(sortField.getOrder()).ignoreUnmapped(true));
        }
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
     *
     * @param request
     * @return
     */
    public static String extractSearchTerm(HttpServletRequest request) {
        String query = getParam(request, "q", getParam(request, "query"));//get query if q not given, list pages use query
        if (StringUtils.isEmpty(query)) {
            return null;
        }
        if (query.length() > 200) {
            throw new BadRequestException("Search query contains too many characters");
        }
        return query;
    }

}
