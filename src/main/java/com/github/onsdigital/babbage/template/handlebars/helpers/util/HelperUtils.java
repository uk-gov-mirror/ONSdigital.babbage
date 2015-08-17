package com.github.onsdigital.babbage.template.handlebars.helpers.util;

import com.github.jknack.handlebars.Options;

import java.math.BigDecimal;

/**
 * Created by bren on 06/07/15.
 */
public class HelperUtils {
    //Compare generic number values ignoring data type of number
    public static boolean isEqualNumbers(Number no1, Number no2) {
        return new BigDecimal(no1.toString()).compareTo(new BigDecimal(no2.toString())) == 0;
    }

    /**
     * If any of the the objects is null comparison is false
     * If both objects are numbers compares numerical values,
     * otherwise uses Java equals comparison using o1.equals(o2)
     *
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isEqual(Options op, Object o1, Object o2) {
        if (op.isFalsy(o1) || op.isFalsy(o2)) {
            return false;
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return isEqualNumbers((Number) o1, (Number) o2);
        }
        return o1.equals(o2);
    }

    public static boolean isNotEqual(Options op, Object o1, Object o2) {
        return !isEqual(op, o1, o2);
    }

}

