package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bren on 06/07/15.
 */
public enum ArrayHelpers implements BabbageHandlebarsHelper<List> {

    contains {
        @Override
        public CharSequence apply(List collection, Options options) throws IOException {
            Object value = options.param(0);
            if (options.isFalsy(collection)) {
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

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    };


}