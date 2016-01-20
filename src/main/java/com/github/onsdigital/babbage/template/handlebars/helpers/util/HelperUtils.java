package com.github.onsdigital.babbage.template.handlebars.helpers.util;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Handlebars.Utils;
import org.apache.commons.lang3.StringUtils;

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
    public static boolean isEqual(Object o1, Object o2) {
        if (Utils.isEmpty(o1) || Utils.isEmpty(o2)) {
            return false;
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return isEqualNumbers((Number) o1, (Number) o2);
        }
        return o1.equals(o2);
    }


    public static int compare(Comparable o1, Comparable o2) {
        if (Utils.isEmpty(o1)) {
            return 1;
        }
        if (Utils.isEmpty(o2)) {
            return -1;
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            return new BigDecimal(o1.toString()).compareTo(new BigDecimal(o2.toString()));
        }
        return o1.compareTo(o2);
    }


    public static boolean isNotEqual(Object o1, Object o2) {
        return !isEqual(o1, o2);
    }


    public static Double toNumber(Object object) {
        if (object == null) {
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

