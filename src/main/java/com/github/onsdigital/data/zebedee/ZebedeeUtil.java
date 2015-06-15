package com.github.onsdigital.data.zebedee;

import javax.servlet.http.Cookie;

/**
 * Created by bren on 13/06/15.
 */
public class ZebedeeUtil {


    public static ZebedeeRequest getZebedeeRequest(String uri, Cookie[] cookies) {

        if (cookies == null) {
            return null;
        }

        String collection = null;
        String accessToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("collection")) {
                System.out.println("Found collection cookie: " + cookie.getValue());
                collection = cookie.getValue();
            }
            if (cookie.getName().equals("access_token")) {
                System.out.println("Found access_token cookie: " + cookie.getValue());
                accessToken = cookie.getValue();
            }
        }

        if (collection != null) {
            return new ZebedeeRequest(collection, accessToken);
        }

        return null; //No collection token found
    }


}
