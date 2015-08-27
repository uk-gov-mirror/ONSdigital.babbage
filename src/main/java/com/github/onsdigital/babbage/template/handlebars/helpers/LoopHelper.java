package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.EachHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.reverse;

/**
 * Created by bren on 02/07/15.
 * <p/>
 * Repeat content fixed number of times, or array sorted by a field or in reverse order
 */

public class LoopHelper extends EachHelper implements BabbageHandlebarsHelper<Object> {

    public static final String HELPER_NAME = "loop";

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        if (context == null) {
            return "";
        }

        //Delegate everything other than repeat number to each helper
        if (context instanceof Number == false) {
            boolean reverse = Boolean.TRUE.equals(options.hash("reverse"));
            String field = options.hash("orderBy");
            sort((List) context, reverse, field);
            return super.apply(context, options);
        }

        //Create an integer list with given number and delegate to each helper to be able to use all features of each helper (index, last , first etc)
        return super.apply(buildList((Number) context), options);
    }

    private List<Integer> buildList(Number repeatNumber) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        int numberOfRepeat = repeatNumber.intValue();
        for (int i = 0; i < numberOfRepeat; i++) {
            integers.add(i);
        }
        return integers;
    }

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper(HELPER_NAME, this);
    }


    private void sort(List list, boolean reverse, String field) {
        if (field == null) {
            if (reverse) {
                reverse(list);
            } else {
                Collections.sort(list);
            }
        } else {
            Collections.sort(list, new MapFieldComparator<>(field, reverse));
        }

        return;
    }

    /**
     * Compares two map fields. If given object is not map it will fail,
     * not using reflection to get field names. It is sufficient since Babbage deserialise json objects to map for rendering
     *
     * @param <Object>
     */
    private class MapFieldComparator<Object> implements Comparator {

        boolean reverse;
        String field;

        /**
         * Field can be in dot notation e.g. description.releaseDate
         *
         * @param field
         * @param reverse
         */
        private MapFieldComparator(String field, boolean reverse) {
            this.field = field;
            this.reverse = reverse;
        }

        @Override
        public int compare(java.lang.Object o1, java.lang.Object o2) {
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }

            Map m1 = (Map) o1;
            Map m2 = (Map) o2;

            int result = HelperUtils.compare(getField(m1, field), getField(m2, field));
            return reverse ? result * -1 : result;

        }


        private Comparable getField(Map m, String field) {
            if (StringUtils.isEmpty(field)) {
                return null;
            }

            int i = field.indexOf(".");
            if (i < 0) {
                Comparable comparable = (Comparable) m.get(field);
                System.out.println(field + ":" + comparable);
                return comparable;
            } else {
                String firstField = field.substring(0, i);
                System.out.println(firstField +  ":");
                return getField((Map) m.get(firstField), field.substring(i + 1));
            }

        }
    }

}

