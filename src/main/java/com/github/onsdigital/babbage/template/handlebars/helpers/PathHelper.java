package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;
import java.net.URI;

/**
 * Created by bren on 14/07/15.
 *
 * Adds / before uri if not available
 */
public enum PathHelper implements BabbageHandlebarsHelper<String> {

    rootpath   {
        @Override
        public String getHelperName() {
            return "rootpath";
        }

        @Override
        public CharSequence apply(String uri, Options options) throws IOException {
            if(options.isFalsy(uri)) {
                return null;
            }
            return uri.startsWith("/") ? uri : "/" + uri;
        }
    }
}
