//package com.github.onsdigital.babbage.search.helpers;
//
//import com.github.onsdigital.babbage.configuration.Configuration;
//import com.github.onsdigital.babbage.search.ONSQuery;
//import com.github.onsdigital.babbage.search.input.SortBy;
//import com.github.onsdigital.babbage.search.input.TypeFilter;
//import com.github.onsdigital.babbage.search.model.ContentType;
//import com.github.onsdigital.babbage.search.model.field.FilterableField;
//import com.github.onsdigital.babbage.search.model.field.SearchableField;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;
//import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
//import static com.github.onsdigital.babbage.util.RequestUtil.getParams;
//import static com.github.onsdigital.babbage.util.common.EnumUtil.namesOf;
//import static org.apache.commons.lang3.StringUtils.isEmpty;
//import static org.apache.commons.lang3.StringUtils.isNotEmpty;
//
///**
// * Created by bren on 01/10/15.
// * <p/>
// * Extracts common request parameters ( type filters , sorting, etc. ) and builds ONSQuery wrapper based on these parameters.
// * <p/>
// * Resulting wrapper can be altered for further need
// */
//public class SearchRequestQueryBuilder {
//
//    private HttpServletRequest searchRequest;
//    private String topicUri;
//    private ContentType[] allowedTypes;
//
//    public SearchRequestQueryBuilder(HttpServletRequest request, String topicUri, ContentType... allowedTypes) {
//        this.searchRequest = request;
//        this.topicUri = topicUri;
//        this.allowedTypes = allowedTypes;
//    }
//
//    public ONSQuery buildQuery() {
//
//        ONSQuery onsQuery = new ONSQuery()
//                .setTypes(resolveTypesFilter(allowedTypes))
//                .setPage(extractPage(searchRequest))
//                .setSize(Configuration.GENERAL.getResultsPerPage())
//                .setHighLightFields(true);
//
//        addFields(onsQuery);
//        resolveDateFilter(onsQuery);
//        resolveUriPrefix(onsQuery);
//        resolveQuery(onsQuery);
//        resolveKeywords(onsQuery);
//        resolveSorting(onsQuery);
//        return onsQuery;
//    }
//
//    private void addFields(ONSQuery onsQuery) {
//        SearchableField[] fields = SearchableField.values();
//        for (SearchableField field : fields) {
//            onsQuery.addField(field.name(), field.getBoostFactor());
//        }
//    }
//
//    private void resolveDateFilter(ONSQuery query) {
//        String updated = getParam(searchRequest, "updated");
//        Date fromDate;
//        Date toDate = null;
//
//        if (updated == null) {
//            updated = "";
//        }
//
//        switch (updated) {
//            case "today":
//                fromDate = daysBefore(1);
//                break;
//            case "week":
//                fromDate = daysBefore(7);
//                break;
//            case "month":
//                fromDate = daysBefore(30);
//                break;
//            default:
//                fromDate = parseDate(getParam(searchRequest, "fromDate"));
//                toDate = parseDate(getParam(searchRequest, "toDate"));
//                break;
//        }
//        addRangeFilter(query, FilterableField.releaseDate, fromDate, toDate);
//    }
//
//    private Date now() {
//        return new Date();
//    }
//
//    private Date daysBefore(int days) {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, -1 * days);
//        return cal.getTime();
//    }
//
//    private void resolveSorting(ONSQuery onsQuery) {
//        SortBy sortBy = extractSortBy(searchRequest);
//        if (sortBy != null) {
//            addSort(onsQuery, sortBy);
//        } else {
//            addSort(onsQuery, SortBy.RELEVANCE);
//        }
//    }
//
//
//    private void resolveUriPrefix(ONSQuery onsQuery) {
//        String uriPrefix = topicUri;
//        if (isNotEmpty(uriPrefix)) {
//            addPrefixFilter(onsQuery, FilterableField.uri, uriPrefix);
//        }
//    }
//
//    private void resolveKeywords(ONSQuery onsQuery) {
//        String[] keywords = getParams(searchRequest, "keywords");
//        if (keywords != null) {
//            for (String keyword : keywords) {
//                if (StringUtils.isNotEmpty(keyword)) {
//                    addTermFilter(onsQuery, FilterableField.keywords_raw, keyword.trim());
//                }
//            }
//        }
//    }
//
//    private void resolveQuery(ONSQuery onsQuery) {
//        String query = extractSearchTerm(searchRequest);
//        if (isNotEmpty(query)) {
//            onsQuery.setSearchTerm(query);
//        }
//    }
//
//    private String[] resolveTypesFilter(ContentType[] allowedTypes) {
//        TypeFilter[] requestedFilters = getFiltersByName(getParams(searchRequest, "filter"));
//        //if no filter is given use all allowed filters
//        if (requestedFilters == null) {
//            return namesOf(allowedTypes);
//        }
//
//        //return requested filter requestedTypes if no allowed filter list given
//        if (allowedTypes == null) {
//            return getTypesFor(requestedFilters);
//        }
//        return resolveTypeFilter(allowedTypes, requestedFilters);
//    }
//
//    private String[] resolveTypeFilter(ContentType[] allowedTypes, TypeFilter[] requestedFilters) {
//        Set<ContentType> allowedTypeSet = toSet(allowedTypes);
//        String[] typesToQuery = new String[0];
//        for (TypeFilter filter : requestedFilters) {
//            for (ContentType contentType : filter.getTypes()) {
//                if (allowedTypeSet.contains(contentType)) {
//                    typesToQuery = ArrayUtils.add(typesToQuery, contentType.name());
//                }
//            }
//
//        }
//
//        if (typesToQuery.length > 0) {
//            return typesToQuery;
//        } else {
//            return namesOf(allowedTypes);
//        }
//    }
//
//    private TypeFilter[] getFiltersByName(String... filterNames) {
//        if (filterNames == null) {
//            return null;
//        }
//        TypeFilter[] filters = null;
//        for (String filterName : filterNames) {
//            if (isEmpty(filterName)) {
//                continue;
//            }
//            try {
//                filters = ArrayUtils.addAll(filters, TypeFilter.valueOf(filterName.toUpperCase()));
//            } catch (IllegalArgumentException e) {
//                //ignore invalid filter name typed in the url
//                continue;
//            }
//        }
//
//        return filters;
//    }
//
//    private String[] getTypesFor(TypeFilter... filters) {
//        if (filters == null) {
//            return null;
//        }
//        String[] types = new String[0];
//        for (TypeFilter filter : filters) {
//            types = ArrayUtils.addAll(types, namesOf(filter.getTypes()));
//        }
//        return types;
//    }
//
//    private Set<ContentType> toSet(ContentType[] types) {
//        HashSet<ContentType> typeSet = new HashSet<>();
//        for (ContentType type : types) {
//            typeSet.add(type);
//        }
//        return typeSet;
//    }
//
//    private Date parseDate(String date) {
//        if (isNotEmpty(date)) {
//            date = date.trim();
//            try {
//                return new SimpleDateFormat("dd/MM/yyyy").parse(date);
//            } catch (ParseException e) {
//            }
//        }
//        return null;
//    }
//
//}
