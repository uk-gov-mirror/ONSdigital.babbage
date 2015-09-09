package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.search.ONSQueryBuilder;
import com.github.onsdigital.babbage.search.SearchService;
import com.github.onsdigital.babbage.search.query.SortOrder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private String sortField;
    private String query;
    private String keywordsQuery;
    private Date startDate;
    private Date endDate;


    public SearchRequestHelper(HttpServletRequest request, String topicUri, String[] allowedTypes) {
        this.uriPrefix = cleanUri(topicUri);
        this.types = extractTypes(allowedTypes, request);
        this.page = extractPage(request);
        this.size = Configuration.GENERAL.getResultsPerPage();
        this.sortField = request.getParameter("sortBy");
        this.query = request.getParameter("q");
        this.keywordsQuery = extractKeywordsQuery(request);
        this.startDate = extractStartDate(request);
        this.endDate = extractEndDate(request);
    }

    public ONSQueryBuilder buildQuery() {

        ONSQueryBuilder onsQueryBuilder = new ONSQueryBuilder();

        onsQueryBuilder
                .setTypes(types)
                .setFields(getFields())
                .setPage(page)
                .setSize(size);
        if (query != null) {
            onsQueryBuilder.setQuery(query);
        } else if (keywordsQuery != null) {
            onsQueryBuilder.setQuery(keywordsQuery);
        }
        if (isNotEmpty(uriPrefix)) {
            onsQueryBuilder.setUriPrefix(uriPrefix);
        }
        if (isNotEmpty(sortField)) {
            onsQueryBuilder.addSort(sortField.trim(), getSortOrder(sortField));
        }

        return onsQueryBuilder;
    }

    private SortOrder getSortOrder(String sortBy) {
        if (FilterFields.releaseDate.name().equals(sortBy)) {
            return SortOrder.DESC;
        } else {
            return SortOrder.ASC;
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


    private String extractKeywordsQuery(HttpServletRequest request) {
        String[] keywords = request.getParameterValues("keywords");
        if (keywords == null || keywords.length < 1) {
            return null;
        }

        String query = "";
        int i;
        for (i = 0; i < keywords.length - 1; i++) {
            String keyword = keywords[i];
            query += keyword + ",";
        }
        query += keywords[i];

        return query;
    }


    private String[] getFields() {
        if (isNotEmpty(query)) {
            return SearchFields.getAllSearchFields();
        } else if (isNotEmpty(keywordsQuery)) {
            return new String[]{SearchFields.keywords.name()};
        } else {
            return null;
        }
    }

    private Date extractStartDate(HttpServletRequest request) {
        String dayStart = request.getParameter("ds");
        String monthStart = request.getParameter("ms");
        String yearStart = request.getParameter("ys");
        return parseDate(dayStart, monthStart, yearStart);
    }

    private Date extractEndDate(HttpServletRequest request) {
        String dayEnd = request.getParameter("de");
        String monthEnd = request.getParameter("me");
        String yearEnd = request.getParameter("ye");
        return parseDate(dayEnd, monthEnd, yearEnd);
    }

    private Date parseDate(String dayStart, String monthStart, String yearStart) {
        if (isNotEmpty(dayStart) && isNotEmpty(monthStart) && isNotEmpty(yearStart)) {
            try {
                return new SimpleDateFormat("ddMMyyyy").parse(dayStart + monthStart + yearStart);
            } catch (ParseException e) {
                throw new RuntimeException("Parsing start date for filter failed!", e);
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

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getKeywordsQuery() {
        return keywordsQuery;
    }

    public void setKeywordsQuery(String keywordsQuery) {
        this.keywordsQuery = keywordsQuery;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
