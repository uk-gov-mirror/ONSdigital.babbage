package com.github.onsdigital.babbage.search.helpers;

import org.elasticsearch.action.count.CountResponse;

/**
 * Created by bren on 07/09/15.
 */
public class CountResponseHelper {

    private CountResponse response;
    public CountResponseHelper(CountResponse response) {
        this.response = response;
    }

    public long getCount() {
        return response.getCount();
    }

}
