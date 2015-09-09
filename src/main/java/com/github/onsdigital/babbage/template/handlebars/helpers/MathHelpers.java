package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by bren on 02/07/15.
 */
public enum MathHelpers implements BabbageHandlebarsHelper<Object> {

    increment {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num = toDouble(context);
            if (num == null) {
                return null;
            }
            num++;
            return new Handlebars.SafeString(String.valueOf(num.longValue()));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },
    decrement {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num = toDouble(context);
            if (num == null) {
                return null;
            }
            num--;
            return new Handlebars.SafeString(String.valueOf(context));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },


    add {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num1 = toDouble(context);
            Double num2 = toDouble(options.param(0));
            if (num1 == null || num2 == null) {
                return null;
            }

            double result = num1 + num2;
            return new Handlebars.SafeString(String.valueOf(result));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    };

    private static Double toDouble(Object object) {
        if (Handlebars.Utils.isEmpty(object)) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else if (object instanceof String) {
            String numberString = (String) object;
            if (StringUtils.isNotEmpty(numberString)) {
                try {
                    return Double.valueOf(numberString);
                } catch (NumberFormatException e) {
                    System.err.println(object + " is not a number!!");
                    return null;
                }
            }
        }
        return null;
    }
}
