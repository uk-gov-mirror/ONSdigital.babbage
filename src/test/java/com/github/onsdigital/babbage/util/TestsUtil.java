package com.github.onsdigital.babbage.util;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;

public class TestsUtil {

    /**
     * Test helper method to set private field values on the class under test - i.e. mocked classes.
     */
    public static void setPrivateField(Object target, String fieldName, Object newValue) throws NoSuchFieldException,
            IllegalAccessException {
        Field field = null;
        Class clazz = target.getClass();
        boolean hasParent = true;

        while (hasParent) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException ex) {
                if (clazz.getSuperclass().equals(Object.class)) {
                    hasParent = false;
                    warn().data("field", fieldName)
                            .data("class", target.getClass().getSimpleName())
                            .log("Could not find field in class or any of super classes setting to null");
                } else {
                    clazz = clazz.getSuperclass();
                }
            }
        }
        FieldUtils.writeField(field, target, newValue, true);
    }

    /**
     * Test helper method to set private static field values on the class under test - i.e. mocked classes.
     */
    public static void setPrivateStaticField(Object target, String fieldName, Object newValue) throws NoSuchFieldException,
            IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
