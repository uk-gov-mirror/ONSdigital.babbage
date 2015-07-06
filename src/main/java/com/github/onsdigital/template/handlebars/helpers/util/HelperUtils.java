package com.github.onsdigital.template.handlebars.helpers.util;

import java.math.BigDecimal;

/**
 * Created by bren on 06/07/15.
 */
public class HelperUtils {

    //Compare generic number values ignoring data type of number
    public static boolean isEqualNumbers(Number no1, Number no2) {
        return new BigDecimal(no1.toString()).compareTo(new BigDecimal(no2.toString())) == 0;
    }


    public static boolean isEqual(Object o1, Object o2) {
        if(o1 instanceof Number && o2 instanceof Number) {
            return isEqualNumbers((Number) o1, (Number) o2);
        }

        return o1.equals(o2);
    }

}
