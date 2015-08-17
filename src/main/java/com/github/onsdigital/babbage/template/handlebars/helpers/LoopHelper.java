package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.EachHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by bren on 02/07/15.
 *
 * Repeat content fixed number of times
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
            Object reverse = options.hash("reverse");
            if (Boolean.TRUE.equals(reverse)) {
                List list = (List) context;
                Collections.reverse(list);
            }
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

}
