package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;

/**
 * Created by bren on 02/07/15.
 */
public enum ConditionHelpers implements BabbageHandlebarsHelper<Object> {

    eq {
        @Override
        public String getHelperName() {
            return "eq";
        }

        @Override
        public CharSequence apply(Object context, Options o) throws IOException {
            Object value = o.param(0);
            if (o.isFalsy(context) || o.isFalsy(value)) {
                return o.inverse(); //Do not render
            }

            if (HelperUtils.isEqual(context, value)) {
                return o.fn();
            }

            return o.inverse();
        }
    },

    ne {
        @Override
        public String getHelperName() {
            return "ne";
        }

        @Override
        public CharSequence apply(Object context, Options o) throws IOException {
            Object value = o.param(0);
            //If any content is falsy ( see isFalsy method doc, means not equal )
            if (o.isFalsy(context) || o.isFalsy(value)) {
                return o.fn();
            }

            if (HelperUtils.isEqual(context, value)) {
                return o.inverse();
            }

            return o.fn();
        }
    },

    //Render block if all given params are ok
    ifall {
        @Override
        public String getHelperName() {
            return "ifall";
        }

        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            if (options.isFalsy(context)) {
                return options.inverse();
            } else {
                Object[] params = options.params;
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (options.isFalsy(param)) {
                        return options.inverse();
                    }
                }
                return options.fn();
            }
        }
    },

    //Render block if any given params are ok
    ifany {
        @Override
        public String getHelperName() {
            return "ifany";
        }

        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            if (!options.isFalsy(context)) {
                return options.fn();
            } else {
                Object[] params = options.params;
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (!options.isFalsy(param)) {
                        return options.fn();
                    }
                }
                return options.inverse();
            }
        }
    };
}
