package com.github.onsdigital.request;

import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.request.handler.DataRequestHandler;
import com.github.onsdigital.request.handler.PageRequestHandler;
import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.request.handler.base.RequestHandler;
import org.reflections.Reflections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Due to url design data, page, charts are all served through the same endpoint. The request type is denoted after resource uri
 * <p>
 * e.g. /economy/inflationpriceindices will server cpi page, in order to get data /economy/inflationpriceindices is to be used
 * <p>
 * RequestDelegator resolves what type of request is made and delegates flow to appropriate handlers
 */
public class RequestDelegator {

    private static Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();


    //Find request handlers and register
    static {
        resolveRequestHandlers();
    }

    public static Object handle(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String requestType = URIUtil.resolveRequestType(request.getRequestURI());

        RequestHandler handler = handlers.get(requestType);
        if(handler == null) {
            throw new ResourceNotFoundException("Could not found appropriate handlers to handle this request");
        }

    }


    public static void handleDataRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new DataRequestHandler().handleDataRequest(getResourceUri(request), response);
    }

    public static void handlePageRequest(HttpServletRequest request, HttpServletResponse response) {
    }


    private static String getResourceUri(HttpServletRequest request) {
        return URIUtil.resolveResouceUri(request.getRequestURI());
    }

    private static void resolveRequestHandlers() {
        System.out.println("Resolving request handlers");

        Set<Class<? extends RequestHandler>> requestHandlerClasses = new Reflections().getSubTypesOf(RequestHandler.class);
        for (Class<? extends RequestHandler> handlerClass : requestHandlerClasses) {
            String className = handlerClass.getSimpleName();
            System.out.println("Registering request handler: " + className);
            try {
                handlers.put(handlerClass.getSimpleName(), handlerClass.newInstance());
            } catch (Exception e) {
                System.err.println("Failed initializing request handler:" + className);
                e.printStackTrace();
            }
        }

    }
}
