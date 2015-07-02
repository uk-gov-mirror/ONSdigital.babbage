package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.EachHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 02/07/15.
 *
 * Repeat content fixed number of times
 */

public class LoopHelper extends EachHelper {

    public static final String NAME = "loop";

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        if (context == null) {
            return StringUtils.EMPTY;
        }

        //Delegate everything other than repeat number to each helper
        if (context instanceof Number == false) {
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

}
