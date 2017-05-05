package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import java.util.Collections;
import java.util.Map;

import static com.github.onsdigital.babbage.util.URIUtil.isDataRequest;

/**
 * Created by bren on 25/11/15.
 */
@Api
public class TimeSeriesTool {

    @GET
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final boolean dataRequest = isDataRequest(request.getRequestURI());
        final String topic = request.getParameter("topic");
        final SearchParam searchParam = SearchParamFactory.getInstance(request, SortBy.release_date, Collections.singleton(QueryType.SEARCH));
        searchParam.addDocType(ContentType.timeseries)
                .setPrefixURI(topic);

        final Map<String, SearchResult> search = SearchUtils.search(searchParam);

        SearchUtils.buildResponse(dataRequest,
                getClass().getSimpleName(),
                search)
                .apply(request, response);
    }

}
