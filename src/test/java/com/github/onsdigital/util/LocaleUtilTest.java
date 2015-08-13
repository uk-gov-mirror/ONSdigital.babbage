package com.github.onsdigital.util;

import org.junit.Test;

import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class LocaleUtilTest {

    private static final Locale welshLocale = new Locale("cy");

    @Test
    public void getLocaleShouldReturnEnglishForHome() {
        Locale locale = LocaleUtil.getLocaleFromUri("/");
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    public void getLocaleShouldReturnEnglishIfNotRecognised() {
        Locale locale = LocaleUtil.getLocaleFromUri("/some/uri");
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    public void getLocaleShouldReturnEnglish() {
        Locale locale = LocaleUtil.getLocaleFromUri("/en/some/uri");
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    public void getLocaleShouldReturnEnglishWithoutLeadingSlash() {
        Locale locale = LocaleUtil.getLocaleFromUri("en/some/uri");
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    public void getLocaleShouldReturnWelshWithoutLeadingSlash() {
        Locale locale = LocaleUtil.getLocaleFromUri("cy/some/uri");
        assertEquals(welshLocale, locale);
    }

    @Test
    public void getLocaleShouldReturnWelsh() {
        Locale locale = LocaleUtil.getLocaleFromUri("/cy/some/uri");
        assertEquals(welshLocale, locale);
    }

    @Test
    public void trimLanguageShouldDoNothingWithASingleForwardSlash() {
        String path = "/";
        String actual = LocaleUtil.trimLanguage(path);
        assertEquals("/", actual);
    }

    @Test
    public void trimLanguageShouldTrimPathWithLocaleAndLeadingForwardSlash() {
        String path = "/cy/some/path";
        String actual = LocaleUtil.trimLanguage(path);
        assertEquals(path.replace("/cy", ""), actual);
    }

    @Test
    public void trimLanguageShouldTrimPathWithLocale() {
        String path = "cy/some/path";
        String actual = LocaleUtil.trimLanguage(path);
        assertEquals(path.replace("cy", ""), actual);
    }

    @Test
    public void trimLanguageShouldNotTrimAnythingWithoutLocale() {
        String path = "/some/path";
        String actual = LocaleUtil.trimLanguage(path);
        assertEquals(path, actual);
    }

    @Test
    public void trimLanguageShouldTrimPathForHome() {
        String path = "/cy/";
        String actual = LocaleUtil.trimLanguage(path);
        assertEquals("/", actual);
    }
}
