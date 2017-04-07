package com.github.onsdigital.babbage.search.model.filter;

/**
 * Created by guesm on 06/04/2017.
 */
public class UpcomingFilter implements Filter {

    @Override
    public String getKey() {
        return "upcoming";

    }

    @Override
    public String getValue() {
        return "true";
    }
}
