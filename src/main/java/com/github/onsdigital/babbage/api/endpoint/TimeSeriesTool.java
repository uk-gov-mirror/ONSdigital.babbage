package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchRendering;
import com.github.onsdigital.babbage.search.builders.ONSQueryBuilders;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.model.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildListQuery;
import static com.github.onsdigital.babbage.api.util.SearchUtils.searchAll;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterDates;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterTopic;

/**
 * Created by bren on 25/11/15.
 */
@Api
public class TimeSeriesTool {

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SearchRendering.buildResponse(request, getClass().getSimpleName(), searchAll(queries(request)))
                .apply(request, response);
    }

    private SearchQueries queries(HttpServletRequest request) {
        return () -> ONSQueryBuilders.toList(
                buildListQuery(request, filters(request))
                        .types(ContentType.timeseries)
        );
    }

    private SearchFilter filters(HttpServletRequest request) {
        return (listQuery) -> {
            filterTopic(request, listQuery);
            filterDates(request, listQuery);
        };
    }

}
