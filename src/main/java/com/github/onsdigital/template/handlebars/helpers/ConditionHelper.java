package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.template.handlebars.helpers.util.HelperUtils;

import java.io.IOException;

import static com.github.onsdigital.template.handlebars.helpers.util.HelperUtils.isEqualNumbers;

/**
 * Created by bren on 02/07/15.
 */
public enum ConditionHelper implements Helper<Object> {

    eq {
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
    };
}
