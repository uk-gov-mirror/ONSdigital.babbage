package com.github.onsdigital.babbage.request.handler;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.request.handler.PageRequestHandler.*;
import static org.junit.Assert.*;
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
        when(cookie.getName()).thenReturn("cookies_policy");
        when(request.getCookies()).thenReturn(cookies);

        boolean result = isCookiesPreferenceSet(request);

        assertTrue(result);
    }

    @Test(expected = NullPointerException.class)
    public void getCookiesPolicy_NullArgument_ThrowNullPointerException() {
        getCookiesPolicy(null);
    }

    @Test
    public void parseCookiesPolicy_UsageCookiesAccepted_ReturnsTrue() {
        Cookie cookiePolicy = new Cookie("cookies_policy", "{\"essential\":true,\"usage\":true}");

        CookiesPolicy expected = new CookiesPolicy(true, true);
        CookiesPolicy result = parseCookiesPolicy(cookiePolicy.getValue());

        assertEquals(expected.isEssential(), result.isEssential());
    }

    @Test
    public void parseCookiesPolicy_UsageCookieNotAccepted_ReturnsFalse() {
        Cookie cookiePolicy = new Cookie("cookies_policy", "{\"essential\":true,\"usage\":false}");

        CookiesPolicy expected = new CookiesPolicy(true, false);
        CookiesPolicy result = parseCookiesPolicy(cookiePolicy.getValue());

        assertEquals(expected.isUsage(), result.isUsage());
    }

    @Test(expected=NullPointerException.class)
    public void parseCookiesPolicy_NullArgument_ThrowsNullPointerException() {
        parseCookiesPolicy(null);
    }
}