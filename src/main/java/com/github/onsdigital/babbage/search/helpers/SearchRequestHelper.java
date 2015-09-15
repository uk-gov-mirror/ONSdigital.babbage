package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.query.filter.Filter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.github.onsdigital.babbage.util.URIUtil.cleanUri;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by bren on 07/09/15.
 */
public class SearchRequestHelper {

    private String uriPrefix;
    private String[] types;
    private Integer page;
    private Integer size;
    private SortBy sortBy;
    private String query;
    private String[] keywords;
    private Date fromDate;
    private Date toDate;
    private boolean filterLatest;


    public SearchRequestHelper(HttpServletRequest request, String topicUri, String[] allowedTypes) {
        this.uriPrefix = cleanUri(topicUri);
        this.types = extractTypes(allowedTypes, request);
        this.page = extractPage(request);
        this.size = Configuration.GENERAL.getResultsPerPage();
        this.sortBy = extractSortBy(request.getParameter("sortBy"));
        if (sortBy == null) {
            sortBy = SortBy.RELEVANCE;
        }
        this.query = request.getParameter("q");
        this.keywords = request.getParameterValues("keywords");
        this.fromDate = parseDate(request.getParameter("fromDate"));
        this.toDate = parseDate(request.getParameter("toDate"));
    }

    public ONSQueryBuilder buildQuery() {

        ONSQueryBuilder onsQueryBuilder = new ONSQueryBuilder();

        onsQueryBuilder
                .setTypes(types)
                .setFields(SearchFields.values())
                .setPage(page)
                .setSize(size)
                .setHighLightFields(true)
                .addRangeFilter(Fields.releaseDate.name(), fromDate, toDate);
        if (query != null) {
            onsQueryBuilder.setQuery(query);
        }
        if (keywords != null) {
            for (String keyword : keywords) {
                if (StringUtils.isNotEmpty(keyword)) {
                    onsQueryBuilder.addFilter(Filter.FilterType.TERM, keyword, Fields.keywords_raw, keyword.trim());
                }
            }
        }

        if (isNotEmpty(uriPrefix)) {
            onsQueryBuilder.setUriPrefix(uriPrefix);
        }

        if (filterLatest) {
            onsQueryBuilder.addFilter(Fields.latestRelease.name(), true);
        }
        if (sortBy != null) {
            for (Fields sortField : sortBy.getSortFields()) {
                onsQueryBuilder.addSort(sortField, sortField.getSortOrder());
            }
        }

        return onsQueryBuilder;
    }

    private SortBy extractSortBy(String sortBy) {
        if (StringUtils.isEmpty(sortBy)) {
            return null;
        }
        try {
            return SortBy.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
//            given sortBy parameter is not available, ignore
            return null;
        }

    }

    private String[] extractTypes(String[] allowedTypes, HttpServletRequest request) {
        String[] types = request.getParameterValues("type");
        if (types == null || types.length == 0) {
            return allowedTypes;
        }

        if (allowedTypes == null) {
            return types;
        }

        Set<String> allowedTypeSet = toSet(allowedTypes);
        String[] typesToQuery = new String[0];
        for (String type : types) {
            if (allowedTypeSet.contains(type)) {
                typesToQuery = ArrayUtils.add(typesToQuery, type);
            }
        }
        if (typesToQuery.length > 0) {
            return typesToQuery;
        } else {
            return allowedTypes;
        }
    }

    private Set<String> toSet(String[] types) {
        HashSet<String> typeSet = new HashSet<>();
        for (String type : types) {
            typeSet.add(type);
        }
        return typeSet;
    }

    /**
     * Extract the page number from a request - for paged results.
     *
     * @param request
     * @return
     */
    private static int extractPage(HttpServletRequest request) {
        String page = request.getParameter("page");

        if (StringUtils.isEmpty(page)) {
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

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywordsQuery(String... keywords) {
        this.keywords = keywords;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isFilterLatest() {
        return filterLatest;
    }

    public void setFilterLatest(boolean filterLatest) {
        this.filterLatest = filterLatest;
    }
}
