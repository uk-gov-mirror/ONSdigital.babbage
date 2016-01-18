package com.github.onsdigital.babbage.util;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by dave on 1/4/16.
 */
public class TestsUtil {

	/**
	 * Test helper method to set private field values on the class under test - i.e. mocked classes.
	 */
	public static void setPrivateField(Object target, String fieldName, Object newValue) throws NoSuchFieldException,
			IllegalAccessException {
		Field field = target.getClass().getDeclaredField(fieldName);
		FieldUtils.writeField(field, target, newValue, true);
	}

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
