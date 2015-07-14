package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.net.URI;

/**
 * Created by bren on 14/07/15.
 *
 * Adds / before uri if not available
 */
public enum PathHelper implements Helper<URI> {

    rootpath   {
        @Override
        public CharSequence apply(URI uri, Options options) throws IOException {
            if(options.isFalsy(uri)) {
                return null;
            }
            return uri.toString().startsWith("/") ? uri.toString() : "/" + uri;
        }
    }
}
