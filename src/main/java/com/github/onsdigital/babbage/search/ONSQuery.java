//package com.github.onsdigital.babbage.search;
//
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.index.query.FilterBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilder;
//import org.elasticsearch.search.sort.SortBuilder;
//
//import java.util.*;
//
///**
// * Created by bren on 07/09/15.
// * <p/>
// * <p/>
// * A wrapper-builder for building a filterable, multiple field match Elasticsearch query which can be executed against specified Elasticsearch types and can be sorted.
// * <p/>
// * Specifying a page is also possible by setting page and size properties. (e.g. a size 20 and page 3 will return results from 41 to 60)
// * <p/>
// * if a search term is not set will be just a filter query with no highlighting (even if highlighting is set) or scoring.
// */
//public class ONSQuery {
//    static final String HIGHLIGHTER_PRE_TAG = "<strong>";
//    static final String HIGHLIGHTER_POST_TAG = "</strong>";
//
//    private String[] types;
//    private Map<String, Long> fields = new HashMap<>();//fieldName, boostFactor mapping
//    private String searchTerm;
//    private List<FilterBuilder> filters = new ArrayList<>();
//    private List<AggregationBuilder> aggregations = new ArrayList<>();
//    private List<SortBuilder> sorts = new ArrayList<>();
//    private boolean highLightFields;
//    private Integer page;
//    private Integer size;
//
//    /**
//     * Initializes query with optional types, if not types are passed query will be for all Elasticsearch types
//     *
//     * @param types optional Elasticsearch types to run query against
//     */
//    public ONSQuery(String... types) {
//        this.types = types;
//    }
//
//    public String[] getTypes() {
//        return types;
//    }
//
//    /**
//     * Sets (overwrites existing) types
//     *
//     * @param types Elasticsearch types to run query against
//     * @return
//     */
//    public ONSQuery setTypes(String... types) {
//        this.types = types;
//        return this;
//    }
//
//    public String[] getFields() {
//        return fields.keySet().toArray(new String[fields.size()]);
//    }
//
//    public String[] getBoostedFields() {
//        String[] boostedFieldNames = new String[fields.size()];
//        Iterator<Map.Entry<String, Long>> iterator = fields.entrySet().iterator();
//
//        for (int i = 0; i < boostedFieldNames.length; i++) {
//            Map.Entry<String, Long> next = iterator.next();
//            Long boost = next.getValue();
//            boostedFieldNames[i] = next.getKey() + (boost == null ? "" : "^" + boost);
//        }
//        return boostedFieldNames;
//    }
//
//    /**
//     * Sets (overwrites existing) fields to match query
//     *
//     * @param fields
//     * @return
//     */
//    public ONSQuery setFields(String... fields) {
//        this.fields = new HashMap<>();
//        if (fields == null) {
//            return this;
//        }
//        for (String field : fields) {
//            this.fields.put(field, null);
//        }
//        return this;
//    }
//
//    public ONSQuery addField(String field, Long boostFactor) {
//        this.fields.put(field, boostFactor);
//        return this;
//    }
//
//    public String getSearchTerm() {
//        return searchTerm;
//    }
//
//    public ONSQuery setSearchTerm(String searchTerm) {
//        this.searchTerm = searchTerm;
//        return this;
//    }
//
//    public List<FilterBuilder> getFilters() {
//        return filters;
//    }
//
//    public ONSQuery setFilters(List<FilterBuilder> filters) {
//        this.filters = filters;
//        return this;
//    }
//
//    public List<AggregationBuilder> getAggregations() {
//        return aggregations;
//    }
//
//    /**
//     * Adds filter to query, all filters added are added to a single and filter, thus every filter added will narrow the result set down
//     *
//     * @param filterBuilder
//     * @return
//     */
//    public ONSQuery addFilter(FilterBuilder filterBuilder) {
//        filters.add(filterBuilder);
//        return this;
//    }
//
//    public ONSQuery addAggregation(AggregationBuilder aggregationBuilder) {
//        aggregations.add(aggregationBuilder);
//        return this;
//    }
//
//    public Integer getPage() {
//        return page;
//    }
//
//    public ONSQuery setPage(int page) {
//        this.page = page;
//        return this;
//    }
//
//    public Integer getSize() {
//        return size;
//    }
//
//    public ONSQuery setSize(int size) {
//        this.size = size;
//        return this;
//    }
//
//    public Integer getFrom() {
//        if (page == null || size == null) {
//            return null;
//        }
//        return getSize() * (getPage() - 1);
//    }
//
//    public boolean isHighLightFields() {
//        return highLightFields && StringUtils.isNotEmpty(getSearchTerm());
//    }
//
//    public ONSQuery setHighLightFields(boolean highLightFields) {
//        this.highLightFields = highLightFields;
//        return this;
//    }
//
//    public ONSQuery addSort(SortBuilder sort) {
//        this.sorts.add(sort);
//        return this;
//    }
//
//    public List<SortBuilder> getSorts() {
//        return sorts;
//    }
//
//}
