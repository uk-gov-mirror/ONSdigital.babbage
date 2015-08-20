package com.github.onsdigital.babbage.api.filter;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.babbage.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bren on 15/08/15.
 */
public class RequestContext implements Filter {
    @Override
    public boolean filter(HttpServletRequest req, HttpServletResponse res) {
        RequestUtil.clearContext();//clear if any request context bound to this thread before
        RequestUtil.saveRequestContext(req);
        return true;
    }
}
