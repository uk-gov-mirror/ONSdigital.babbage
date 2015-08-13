package com.github.onsdigital.babbage.template.handlebars.helpers.base;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;

/**
 * Created by bren on 11/08/15.
 */
public interface BabbageHandlebarsHelper<T> extends Helper<T> {

    void register(Handlebars handlebars);

}
