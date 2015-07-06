package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by bren on 06/07/15.
 */
public enum ArrayHelpers implements Helper<Collection>{

    contains {
        @Override
        public CharSequence apply(Collection collection, Options options) throws IOException {
            Object value = options.param(0);
            if (options.isFalsy(collection) ) {
                return options.inverse();
            }

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
                Object next = iterator.next();
                if (HelperUtils.isEqual(next, value)) {
                    return options.fn();
                }
            }

            return options.inverse();
        }
    }


}