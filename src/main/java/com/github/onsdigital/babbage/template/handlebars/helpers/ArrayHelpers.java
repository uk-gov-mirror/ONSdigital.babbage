package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by bren on 06/07/15.
 */
public enum ArrayHelpers implements BabbageHandlebarsHelper<Collection> {

    contains {
        @Override
        public String getHelperName() {
            return "contains";
        }

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
