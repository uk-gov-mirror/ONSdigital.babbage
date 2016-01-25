package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
import static org.apache.commons.lang3.EnumUtils.getEnum;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Common utilities to manipulate search request query and extracting common search parameters
 */

public class SearchRequestHelper {

    public static Date[] extractPublishDates(HttpServletRequest request) {
        String updated = request.getParameter("updated");
        Date fromDate;
        Date toDate = null;

        if (updated == null) {
            updated = "";
        }

        switch (updated) {
            case "today":
                fromDate = daysBefore(1);
                break;
            case "week":
                fromDate = daysBefore(7);
                break;
            case "month":
                fromDate = daysBefore(30);
                break;
            default:
                fromDate = parseDate(request.getParameter("fromDate"));
                toDate = parseDate(request.getParameter("toDate"));
                break;
        }
        return new Date[]{fromDate, toDate};
    }

    private static Date parseDate(String date) {
        if (isNotEmpty(date)) {
            date = date.trim();
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(date);
            } catch (ParseException e) {
                //ignore invalid date input
            }
        }
        return null;
    }


    private static Date daysBefore(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1 * days);
        return cal.getTime();
    }

    /**
     * Extracts filter parameters requested, including only filter if given default filters, otherwise ignores.
     * <p/>
     * If there are no valid filters will return default filters
     *
     * @param request
     * @param defaultFilters
     * @return
     */
    public static Set<TypeFilter> extractSelectedFilters(HttpServletRequest request, Set<TypeFilter> defaultFilters) {
        String[] filters = request.getParameterValues("filter");
        if (filters == null) {
            return defaultFilters;
        }

        HashSet<TypeFilter> selectedFilters = new HashSet<>();
        for (int i = 0; i < filters.length; i++) {
            TypeFilter typeFilter = getEnum(TypeFilter.class, upperCase(filters[i]));
            if (defaultFilters.contains(typeFilter)) {
                selectedFilters.add(typeFilter);
            }
        }
        return selectedFilters.isEmpty() ? defaultFilters : selectedFilters;
    }


    public static SortBy extractSortBy(HttpServletRequest request, SortBy defaultSort) {
        String sortBy = getParam(request, "sortBy");
        if (isEmpty(sortBy)) {
            return defaultSort;
        }
        try {
            return SortBy.valueOf(sortBy.toLowerCase());
        } catch (IllegalArgumentException e) {
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
