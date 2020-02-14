package com.github.onsdigital.babbage.request.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.request.handler.PageRequestHandler.isCookiesPreferenceSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PageRequestHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Cookie cookie;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isCookiesPreferenceSet_RequestIsNull_ReturnsFalse() {
        boolean result = isCookiesPreferenceSet(null);
        assertFalse(result);
    }

    @Test
    public void isCookiesPreference_CookiesIsNull_ReturnsFalse() {
        when(request.getCookies()).thenReturn(null);
        boolean result = isCookiesPreferenceSet(request);
        assertFalse(result);
    }

    @Test
    public void isCookiesPreference_CookiesIsEmptyArray_ReturnsFalse() {
        Cookie[] cookies = new Cookie[]{};
        when(request.getCookies()).thenReturn(cookies);
        boolean result = isCookiesPreferenceSet(request);
        assertFalse(result);
    }

    @Test
    public void isCookiesPreference_CookieNotExist_ReturnsFalse() {
        Cookie[] cookies = new Cookie[]{cookie};
        when(cookie.getName()).thenReturn("test_cookie");
        when(request.getCookies()).thenReturn(cookies);

        boolean result = isCookiesPreferenceSet(request);

        assertFalse(result);
    }

    @Test
    public void isCookiesPreference_CookieExists_ReturnsTrue() {
        Cookie[] cookies = new Cookie[]{cookie};
        when(cookie.getName()).thenReturn("cookies_preferences_set");
        when(request.getCookies()).thenReturn(cookies);

        boolean result = isCookiesPreferenceSet(request);

        assertTrue(result);
    }
}