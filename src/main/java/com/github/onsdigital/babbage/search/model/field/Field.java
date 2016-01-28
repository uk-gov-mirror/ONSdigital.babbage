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
    title("description.title", 10,true),
    title_no_stem("description.title.title_no_stem", 10),
    edition("description.edition",true),
    summary("description.summary",true),
    releaseDate("description.releaseDate"),
    metaDescription("description.metaDescription",true),
    keywords("description.keywords",true),
    _type("_type"),
    cdid("description.cdid",true),
    datasetId("description.datasetId",true),
    searchBoost("searchBoost",100),
    latestRelease("description.latestRelease"),
    published("description.published"),
    cancelled("description.cancelled"),
    topics("topics");

    private String fieldName;
    private long boost;
    private static String[] highlightedFields;

    Field(String fieldName, long boost, boolean highlight) {
        this(fieldName);
        this.boost = boost;
        if (highlight) {
            addHighligtedField(fieldName);
        }
    }

    Field(String fieldName, long boost) {
        this(fieldName, boost, false);
    }

    Field(String fieldName, boolean highlight) {
        this(fieldName, 0, highlight);
    }

    Field(String fieldName) {
        this.fieldName = fieldName;
    }

    private void addHighligtedField(String fieldName) {
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

    public static String[] highlightedFieldNames() {
        return highlightedFields;
    }

    public static String[] fieldNames(Field... fields) {
        String[] types = new String[0];
        for (Field field : fields) {
            types = ArrayUtils.addAll(types, field.fieldName());
        }
        return types;
    }

}
