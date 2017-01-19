package com.github.onsdigital.babbage.search.model.field;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by bren on 08/09/15.
 * <p/>
 * Searchable field names with boost factors
 */

//Elastic search 2.0 requires full path of the field when querying ( e.g. description.title.title_no_dates ).
// It is not longer possible to use name of the enum field as it would have dots in it. So added a name field and getter method fieldName for it.
public enum Field {
    uri("uri"),
    _score("_score"), //elastic search internal search score field in results
    title_no_dates("description.title.title_no_dates", 10),
    title_first_letter("description.title.title_first_letter"),
    title_raw("description.title.title_raw"),
    title("description.title", 10, new Highlight(0, 0), false),
    title_no_stem("description.title.title_no_stem", 10),
    title_no_synonym_no_stem("description.title.title_no_synonym_no_stem"), //used for suggestions
    edition("description.edition", new Highlight(0, 0)),
    summary("description.summary", new Highlight(0, 0)),
    releaseDate("description.releaseDate"),
    metaDescription("description.metaDescription", new Highlight(0, 0)),
    keywords("description.keywords", new Highlight(0, 0)),
    _type("_type"),
    cdid("description.cdid", new Highlight(0, 0)),
    datasetId("description.datasetId", new Field.Highlight(0, 0)),
    searchBoost("searchBoost", 100),
    latestRelease("description.latestRelease"),
    published("description.published"),
    cancelled("description.cancelled"),
    topics("topics"),
    content("downloads.content", 10, new Highlight(30, 5), true),
    downloads("downloads*", true);

    private static Field[] highlightedFields;
    /**
     * Excluded sources contains the fields in Elastic Search that are excluded from being returned.
     * In general Babbage only returns the highlighted fields that returned from the query in the "_source"
     * Because teh downloads.content is a large text field made up from the whole of the PDF, XSL etc... these fields need to be
     * excluded from the source on the request and also Babbage needs to know to included the highlighted only fields in the response.
     */

    private static Field[] excludedSource;
    private String fieldName;
    private long boost;
    private Highlight highlight;

    Field(String fieldName, long boost, Highlight highlight, boolean excluded) {
        this.fieldName = fieldName;
        this.boost = boost;
        this.highlight = highlight;
        if (null != highlight) {
            addHighlightedField(this);
        }
        if (excluded) {
            addExcluded(this);
        }
    }

    Field(String fieldName, long boost) {
        this(fieldName, boost, null, false);
    }

    Field(String fieldName, Highlight highlight) {
        this(fieldName, 0, highlight, false);
    }

    Field(String fieldName) {
        this(fieldName, 0, null, false);
    }

    Field(final String fieldName, final boolean excluded) {
        this(fieldName, 0, null, excluded);
    }

    public static Field[] highlightedFields() {
        return highlightedFields;
    }

    public static String[] fieldNames(Field... fields) {
        String[] types = null;
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                types = ArrayUtils.addAll(types, field.fieldName());
            }
        }
        return types;
    }

    public static Field[] excludedSource() {
        return excludedSource;
    }

    private void addExcluded(final Field field) {
        excludedSource = ArrayUtils.add(excludedSource, field);
    }

    private void addHighlightedField(Field fieldName) {
        highlightedFields = ArrayUtils.add(highlightedFields, fieldName);
    }

    /**
     * @return Name of the field with elastic search boost value (e.g. title^10)
     */
    public String fieldNameBoosted() {
        return fieldName + (boost == 0 ? "" : "^" + boost);
    }

    public String fieldName() {
        return fieldName;
    }

    public Long boost() {
        return boost;
    }

    public Highlight highlight() {
        return highlight;
    }

    /**
     * Babbage by default need all the content of the field that is included in the highlight. This will not work with
     * content that is extracted from download files (i.e. PDF,XSL,DOC, etc...)  so we need to be able to configure
     * the highlighting sizing and number per field
     */
    public static final class Highlight {
        /**
         * the number of characters to be included around the text that has been **FOUND**
         */
        private final int fragmentSize;
        /**
         * the number of sections of text to display
         */
        private final int numberOfFragments;

        private Highlight(final int fragmentSize, final int numberOfFragments) {
            this.fragmentSize = fragmentSize;
            this.numberOfFragments = numberOfFragments;
        }

        public int getFragmentSize() {
            return fragmentSize;
        }

        public int getNumberOfFragments() {
            return numberOfFragments;
        }
    }


}
