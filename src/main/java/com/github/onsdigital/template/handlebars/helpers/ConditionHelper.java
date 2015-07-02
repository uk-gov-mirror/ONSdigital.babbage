package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.apache.commons.lang.ObjectUtils;

import java.io.IOException;

/**
 * Created by bren on 02/07/15.
 */
public enum ConditionHelper implements Helper<Object> {

    eq {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Object operand2 = options.param(0);
            if (options.isFalsy(context) || options.isFalsy(operand2)) {
                return options.inverse(); //Do not render
            }

            if (context.equals(operand2)) {
                return options.fn(); //render
            }
            return options.inverse();
        }


    }


}
