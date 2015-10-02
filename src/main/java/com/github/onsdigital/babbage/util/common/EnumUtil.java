package com.github.onsdigital.babbage.util.common;

/**
 * Created by bren on 01/10/15.
 */
public class EnumUtil {

    /**
     * Returns names of given enum code as an array of strings
     *
     * @return array of names for given enum codes
     */
    public static <E extends Enum<E>> String[] namesOf(E... types) {
        if (types == null) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].name();
        }
        return names;
    }
}
