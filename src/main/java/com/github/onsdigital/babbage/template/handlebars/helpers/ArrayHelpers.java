package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 06/07/15.
 */
public enum ArrayHelpers implements BabbageHandlebarsHelper<Object> {

    contains {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Object value = options.param(0);
            if (options.isFalsy(context)) {
                return options.inverse();
            }

            Collection collection = (Collection) context;

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

    },

    /**
     * Used for getting array element using array index or object property using property name (key)
     *
     * Handlebars lookup helper does the same thing, but Handlebars.java implementation is buggy and not exactly compatible with Handlebars.js lookup helper.
     *
     * Opened an issue regarding this on: https://github.com/jknack/handlebars.java/issues/418
     *
     * In the mean time created get Handlebars helper for this purpose
     */
    get {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            if (options.params.length <= 0) {
                return context.toString();
            }
            if (context == null) {
                return null;
            }

            Object lookup = null;
            if (context instanceof Map) {
                lookup = ((Map) context).get(options.param(0));
            } else if (context instanceof List) {
                lookup = ((List) context).get(options.<Integer>param(0));
            } else {
                throw new RuntimeException("Object property resolution is not supported yet");
            }
            if (lookup == null) {
                return null;
            }
            return lookup.toString();
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }

}