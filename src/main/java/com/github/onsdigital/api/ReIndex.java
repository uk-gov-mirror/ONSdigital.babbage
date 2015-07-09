package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.search.Indexer;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by bren on 09/07/15.
 */
@Api
public class ReIndex {

    private static final String REINDEX_KEY = "e48041e14e78978e1c44b23f3979d8e6";

    @POST
    public Object reIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

        synchronized (REINDEX_KEY) {
            try {
                String key = request.getParameter("key");
                if (REINDEX_KEY.equals(key)) {
                    System.out.println("Triggering reindex");
                    Indexer.loadIndex(ElasticSearchServer.getClient());
                } else {
                    return "Wrong key, make sure you pass in the right key";
                }
            } catch (Exception e) {
                System.out.println("Indexing error");
                System.out.println(ExceptionUtils.getStackTrace(e));
                ApiErrorHandler.handle(e, response);
            }
            return "Elasticsearch: indexing complete";
        }

    }


}
