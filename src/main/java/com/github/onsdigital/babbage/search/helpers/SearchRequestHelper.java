package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.response.BabbageRedirectResponse;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.function.SearchFunction;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

import static com.github.onsdigital.babbage.api.util.ListUtils.getBaseListTemplate;
import static com.github.onsdigital.babbage.search.model.field.Field.*;
import static com.github.onsdigital.babbage.util.RequestUtil.getParam;
import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;
import static org.apache.commons.lang3.EnumUtils.getEnum;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS;
import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.weightFactorFunction;

/**
 * Common utilities to manipulate search request query and extracting common search parameters
 */

public class SearchRequestHelper {


    public static void get(HttpServletRequest request, HttpServletResponse response, String searchTerm, String listType, SearchFunction search) throws Exception {
        if (searchTerm == null) {
            buildResponse(request, response, listType, null);
        } else {
            //search time series by cdid, redirect to time series page if found
            String timeSeriesUri = searchTimeSeriesUri(searchTerm);
            if (timeSeriesUri != null) {
                new BabbageRedirectResponse(timeSeriesUri).apply(request, response);
                return;
            }
            buildResponse(request, response, listType, search.search()).apply(request, response);
        }
    }

    //Send result back to client
    private static BabbageResponse buildResponse(HttpServletRequest request, HttpServletResponse response, String listType, Map<String, SearchResult> results) throws IOException {
        LinkedHashMap<String, Object> data = getBaseListTemplate(listType);
        if (results != null) {
            for (Map.Entry<String, SearchResult> result : results.entrySet()) {
                data.put(result.getKey(), result.getValue());
            }
        }
        BabbageResponse result;
        if (isDataRequest(request.getRequestURI())) {
            result = new BabbageStringResponse(JsonUtil.toJson(data), MediaType.APPLICATION_JSON);
        } else {
            result = new BabbageStringResponse(TemplateService.getInstance().renderContent(data), MediaType.TEXT_HTML);
        }
        return result;
    }

    private static String searchTimeSeriesUri(String searchTerm) {
        SearchResponseHelper search = SearchHelper.
                search(onsQuery(boolQuery().must(termQuery(cdid.fieldName(), searchTerm)), ContentType.timeseries)
                        .highlight(false).size(1).fetchFields(Field.uri));

        if (search.getNumberOfResults() == 0) {
            return null;
        }
        Map<String, Object> timeSeries = search.getResult().getResults().iterator().next();
        return (String) timeSeries.get(Field.uri.fieldName());
    }



    /**
     * Extracts filter, sort and page information from given client request to initialize query with
     *
     * @param request
     * @param queryBuilder
     * @param defaultFilters
     * @return
     */
    public static ONSQueryBuilder onsQuery(HttpServletRequest request, QueryBuilder queryBuilder, Set<TypeFilter> defaultFilters) {
        return new ONSQueryBuilder(queryBuilder, extractTypeNames(request, defaultFilters))
                .page(extractPage(request))
                .sortBy(extractSortBy(request));
    }


    /**
     * Extracts sort and page information from given client request to initialize query with
     *
     * @param request
     * @param queryBuilder
     * @param types types to be queried
     * @return
     */
    public static ONSQueryBuilder onsQuery(HttpServletRequest request, QueryBuilder queryBuilder, ContentType... types) {
        return new ONSQueryBuilder(queryBuilder, resolveTypeNames(types))
                .page(extractPage(request))
                .sortBy(extractSortBy(request));
    }

    /**
     * Creates query builder filtering only given content types
     *
     * @param queryBuilder
     * @param typeNames
     * @return
     */
    public static ONSQueryBuilder onsQuery(QueryBuilder queryBuilder, String... typeNames) {
        return new ONSQueryBuilder(queryBuilder, typeNames);
    }

    /**
     * Creates query builder filtering only given content types
     *
     * @param queryBuilder
     * @param types
     * @return
     */
    public static ONSQueryBuilder onsQuery(QueryBuilder queryBuilder, ContentType... types) {
        return new ONSQueryBuilder(queryBuilder, resolveTypeNames(types));
    }

    /**
     * Base content query with common fields in all content types as dis max query.
     *
     * @param searchTerm
     * @return
     */
    public static DisMaxQueryBuilder buildBaseContentQuery(String searchTerm) {
        return disMaxQuery()
                .add(boolQuery()
                        .should(matchQuery(title_no_dates.fieldName(), searchTerm)
                                        .boost(title_no_dates.boost())
                                        .minimumShouldMatch("1<-2 3<80% 5<60%")
                        )
                        .should(multiMatchQuery(searchTerm, title.fieldNameBoosted(), edition.fieldNameBoosted())
                                .type(CROSS_FIELDS).minimumShouldMatch("3<80% 5<60%")))
                .add(multiMatchQuery(searchTerm, summary.fieldNameBoosted(), metaDescription.fieldNameBoosted())
                        .type(BEST_FIELDS).minimumShouldMatch("75%"))
                .add(matchQuery(keywords.fieldNameBoosted(), searchTerm).operator(AND))
                .add(multiMatchQuery(searchTerm, cdid.fieldNameBoosted(), datasetId.fieldNameBoosted()).operator(AND));
    }

    public static ONSQueryBuilder countDocTypes(QueryBuilder query, ContentType... types) {
        return onsQuery(query, types)
                .size(0).aggregate(AggregationBuilders.terms("docCounts")
                        .field(Field._type.name())); //aggregating all content types without using selected numbers
    }

    /**
     *
     * Boosts some content types to be more relevant than others
     *
     * @return
     */
    public static FunctionScoreQueryBuilder boostContentTypes(QueryBuilder query) {
        FunctionScoreQueryBuilder builder = functionScoreQuery(query);
        return addContentBoosts(builder);
    }

    //Adds content type boosts as weight functions if content type is in selected filters
    private static FunctionScoreQueryBuilder addContentBoosts(FunctionScoreQueryBuilder builder) {
        for (ContentType contentType : ContentType.values()) {
            if (contentType.getWeight() != null) {
                builder.add(termQuery(_type.fieldName(), contentType.name()), weightFactorFunction(contentType.getWeight()));
            }
        }
        return builder;
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


    public static SortBy extractSortBy(HttpServletRequest request) {
        String sortBy = getParam(request, "sortBy");
        if (isEmpty(sortBy)) {
            return null;
        }
        try {
            return SortBy.valueOf(sortBy.toLowerCase());
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

    /**
     * Resolves content types to be queried based on selected filters, if no filters submitted will return default filters
     */
    public static String[] extractTypeNames(HttpServletRequest request, Set<TypeFilter> defaultFilters) {
        Set<TypeFilter> selectedFilters = extractSelectedFilters(request, defaultFilters);
        return resolveTypeNames(selectedFilters);
    }

    public static String[] resolveTypeNames(Set<TypeFilter> filters) {
        String[] types = new String[0];
        for (TypeFilter selectedFilter : filters) {
            ContentType[] contentTypes = selectedFilter.getTypes();
            types = ArrayUtils.addAll(types, resolveTypeNames(contentTypes));
        }
        return types;
    }
    public static ContentType[] resolveContentTypes(Set<TypeFilter> filters) {
        return resolveContentTypes(filters.toArray(new TypeFilter[filters.size()]));
    }

    public static ContentType[] resolveContentTypes(TypeFilter... filters) {
        ContentType[] contentTypes = new ContentType[0];
        for (TypeFilter filter : filters) {
            contentTypes = ArrayUtils.addAll(contentTypes, filter.getTypes());
        }
        return contentTypes;
    }

    private static String[] resolveTypeNames(ContentType... contentTypes) {
        String[] types = new String[0];
        for (ContentType type : contentTypes) {
            types = ArrayUtils.addAll(types, type.name());
        }
        return types;
    }
}
