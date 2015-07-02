package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

/**
 * Created by bren on 02/07/15.
 */
public enum MathHelper implements Helper<Integer> {

    increment {
        @Override
        public CharSequence apply(Integer context, Options options) throws IOException {
            if(context != null) {
                context++;
            }
            return new Handlebars.SafeString(String.valueOf(context));
        }
    },
    decrement {
        @Override
        public CharSequence apply(Integer context, Options options) throws IOException {
            if(context != null) {
                context--;
            }
            return new Handlebars.SafeString(String.valueOf(context));
        }
    }
}
