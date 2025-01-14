package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;

/**
 * Created by bren on 16/08/15.
 */
public enum StringHelper implements BabbageHandlebarsHelper<String> {

    //Concat given strings
    concat {
        @Override
        public CharSequence apply(String context, Options options) throws IOException {
            if (options.isFalsy(context)) {
                return null;
            }

            Object[] params = options.params;
            for (Object param : params) {
                if (!options.isFalsy(param))
                    context += param;
            }

            return context;
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    lowercase {
        @Override
        public CharSequence apply(String context, Options options) throws IOException {
            if (options.isFalsy(context)) {
                return null;
            }
            return context.toLowerCase();
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    startsWith {
        @Override
        public CharSequence apply(String context, Options options) throws IOException {
            if (options.isFalsy(context) || options.params.length == 0) {
                return null;
            }
            return context.startsWith(options.<String>param(0)) ? "true" : null;
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    endsWith {
        @Override
        public CharSequence apply(String context, Options options) throws IOException {
            if (options.isFalsy(context) || options.params.length == 0) {
                return null;
            }
            return context.endsWith(options.<String>param(0)) ? "true" : null;
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },

    containsAny {
        @Override
        public CharSequence apply(String context, Options options) throws IOException {
            if (options.isFalsy(context) || options.params == null) {
                return null;
            }

            String text = context.toLowerCase();
            for (Object param : options.params) {
                if (param != null && text.contains(param.toString().toLowerCase())) {
                    return "true";
                }
            }
            return null;
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }
}
