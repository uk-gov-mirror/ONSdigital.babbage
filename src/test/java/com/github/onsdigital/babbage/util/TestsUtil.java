package com.github.onsdigital.babbage.util;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
					String message = "Could not find field '%s' in class '%s' or any of super classes. Setting null";
					System.out.println(String.format(message, fieldName, target.getClass().getSimpleName()));
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
