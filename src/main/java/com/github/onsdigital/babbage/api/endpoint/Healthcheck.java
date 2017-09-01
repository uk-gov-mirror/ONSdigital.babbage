package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * Created by mrout on 29/08/2017.
 */

@Api
public class Healthcheck {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
