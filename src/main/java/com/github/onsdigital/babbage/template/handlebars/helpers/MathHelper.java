package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;

/**
 * Created by bren on 02/07/15.
 */
public enum MathHelper implements BabbageHandlebarsHelper<Integer> {

    increment {
        @Override
        public CharSequence apply(Integer context, Options options) throws IOException {
            if (context != null) {
                context++;
            }
            return new Handlebars.SafeString(String.valueOf(context));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },
    decrement {
        @Override
        public CharSequence apply(Integer context, Options options) throws IOException {
            if (context != null) {
                context--;
            }
            return new Handlebars.SafeString(String.valueOf(context));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }
}
