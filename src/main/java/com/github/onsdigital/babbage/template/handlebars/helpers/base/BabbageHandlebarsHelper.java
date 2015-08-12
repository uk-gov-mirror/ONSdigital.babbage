package com.github.onsdigital.babbage.template.handlebars.helpers.base;

import com.github.jknack.handlebars.Helper;

/**
 * Created by bren on 11/08/15.
 */
public interface BabbageHandlebarsHelper<T> extends Helper<T> {

    /**
     * @return Name of the helper to be register in handlebars
     */
    String getHelperName();

}
