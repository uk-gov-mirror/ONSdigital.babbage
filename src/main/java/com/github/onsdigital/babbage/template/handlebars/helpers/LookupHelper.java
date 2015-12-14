package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by bren on 26/11/15.
 */
public class LookupHelper implements BabbageHandlebarsHelper {

    /**
     * Used for getting array element using array index or object property using property name (key)
     * <p/>
     * Handlebars lookup helper does the same thing, but Handlebars.java implementation is buggy and not exactly compatible with Handlebars.js lookup helper.
     * <p/>
     * Opened an issue regarding this on: https://github.com/jknack/handlebars.java/issues/418
     * <p/>
     * In the mean time created get Handlebars helper for this purpose
     */
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
            int index = HelperUtils.toNumber(options.param(0)).intValue();
            lookup = ((List) context).get(index);
        } else {
            throw new RuntimeException("Object property resolution is not supported");
        }
        if (lookup == null) {
            return null;
        }
        return lookup.toString();
    }

    @Override
    public void register(Handlebars handlebars) {
        handlebars.registerHelper("get", this);
    }

}
