package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.helpers.dates.PublishDates;
import com.github.onsdigital.babbage.search.helpers.dates.PublishDatesException;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.publishedDates;
import static com.github.onsdigital.babbage.search.helpers.dates.PublishDates.updatedWithinPeriod;
import static org.apache.commons.lang3.EnumUtils.getEnum;
import static org.apache.commons.lang3.StringUtils.upperCase;

/**
 * Common utilities to manipulate search request query and extracting common search parameters
 */

public class SearchRequestHelper {

    static final String FROM_DAY_PARAM = "fromDateDay";
    static final String FROM_MONTH_PARAM = "fromDateMonth";
    static final String FROM_YEAR_PARAM = "fromDateYear";
    static final String TO_DAY_PARAM = "toDateDay";
    static final String TO_MONTH_PARAM = "toDateMonth";
    static final String TO_YEAR_PARAM = "toDateYear";
    static final String UPDATED_PARAM = "updated";
    static final String UPCOMING_PARAM = "upcoming";
    static final String VIEW_PARAM = "view";

    static BiFunction<HttpServletRequest, String, String> extractDayOrMonthDateValue = (request, parameterName) ->
            StringUtils.isEmpty(request.getParameter(parameterName)) ? "" : request.getParameter(parameterName) + "/";

    static BiFunction<HttpServletRequest, String, String> extractYearDateValue = (request, parameterName) ->
            StringUtils.isEmpty(request.getParameter(parameterName)) ? "" : request.getParameter(parameterName);

    static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yy");

    private SearchRequestHelper() {
        // contains only static methods - hide constructor.
    }

    public static PublishDates extractPublishDates(HttpServletRequest request) throws PublishDatesException {
        String updatedPeriod = request.getParameter(UPDATED_PARAM);
        PublishDates publishDates;

        if (StringUtils.isEmpty(updatedPeriod) || updatedPeriod.equals("custom")) {
            publishDates = parseDates(request);
        } else {
            publishDates = updatedWithinPeriod(updatedPeriod);
        }
        return publishDates;
    }

    public static PublishDates parseDates(HttpServletRequest req) throws PublishDatesException {
        String fromDateStr = extractDateStr(req, FROM_DAY_PARAM, FROM_MONTH_PARAM, FROM_YEAR_PARAM);
        String toDateStr = extractDateStr(req, TO_DAY_PARAM, TO_MONTH_PARAM, TO_YEAR_PARAM);
        return publishedDates(fromDateStr, toDateStr, allowFutureAfterDate(req));
    }

    private static String extractDateStr(HttpServletRequest req, String dayKey, String monthKey, String yearKey) {
        StringBuilder stringDate = new StringBuilder();
        stringDate.append(extractDayOrMonthDateValue.apply(req, dayKey));
        stringDate.append(extractDayOrMonthDateValue.apply(req, monthKey));
        stringDate.append(extractYearDateValue.apply(req, yearKey));
        return stringDate.toString();
    }

    /**
     * Extracts filter parameters requested, including only filter if given default filters, otherwise ignores.
     * <p>
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
        for (final String filter : filters) {
            TypeFilter typeFilter = getEnum(TypeFilter.class, upperCase(filter));
            if (defaultFilters.contains(typeFilter)) {
                selectedFilters.add(typeFilter);
            }
        }
        return selectedFilters.isEmpty() ? defaultFilters : selectedFilters;
    }


    private static boolean allowFutureAfterDate(HttpServletRequest request) {
        return UPCOMING_PARAM.equalsIgnoreCase(request.getParameter(VIEW_PARAM));
    }


}
