package com.github.onsdigital.babbage.search.model;

import java.util.Comparator;

/**
 * Created by guidof on 22/03/17.
 */
public enum QueryType {

    SEARCH(0, "result"),
    COUNTS(1, "counts"),
    FEATURED(2, "featuredResult"),
    DEPARTMENTS(3, "departments");

    private final int position;
    private final String text;

    QueryType(final int position, final String text) {
        this.position = position;
        this.text = text;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public  static Comparator<QueryType> positionComparator() {
        return (lhs, rhs) -> {
            if (lhs.getPosition() > rhs.getPosition()) {
                return 1;
            }
            else if((lhs.getPosition() == rhs.getPosition()) ) {
                return 0;
            }
            else {
                return -1;
            }

        };
    }

}
