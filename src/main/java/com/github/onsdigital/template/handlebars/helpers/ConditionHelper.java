package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import javax.naming.Context;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by bren on 02/07/15.
 */
public enum ConditionHelper implements Helper<Object> {

    eq {
        @Override
        public CharSequence apply(Object context, Options o) throws IOException {
            Object operand2 = o.param(0);
            if (o.isFalsy(context) || o.isFalsy(operand2)) {
                return inverse(o); //Do not render
            }

            if(context instanceof Number && operand2 instanceof Number) {
                return isEqualNumbers((Number) context, (Number) operand2) ?  render(o) : inverse(o);
            }

            if (context.equals(operand2)) {
                return render(o); //render
            }
            return inverse(o);
        }
    },

    ne {
        @Override
        public CharSequence apply(Object context, Options o) throws IOException {
            Object operand2 = o.param(0);
            //If any content is falsy ( see isFalsy method doc, means not equal )
            if (o.isFalsy(context) || o.isFalsy(operand2)) {
                return render(o);
            }

            if(context instanceof Number && operand2 instanceof Number) {
                return isEqualNumbers((Number) context, (Number) operand2) ? inverse(o) : render(o);
            }

            if (context.equals(operand2)) {
                return inverse(o);
            }
            return render(o);
        }
    };


    //Compare generic number values ignoring data type of number
    private static boolean isEqualNumbers(Number no1, Number no2) {
        return new BigDecimal(no1.toString()).compareTo(new BigDecimal(no2.toString())) == 0;
    }

    private static CharSequence render(Options options) throws IOException {
        return  options.fn();
    }

    private static CharSequence inverse(Options options) throws IOException {
        return  options.inverse();
    }

}
