package com.github.onsdigital.request;

import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.request.handler.base.RequestHandler;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        registerRequestHandlers();
    }

    public static Object handle(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String fullUri = URIUtil.cleanUri(request.getRequestURI());
        String requestType = URIUtil.resolveRequestType(fullUri);
        RequestHandler handler = resolveRequestHandler(requestType);
        if (handler == null) {
            handler = handlers.get("/"); //default handler
            return handler.handle(fullUri, request, response);
        } else {
            return handler.handle(URIUtil.resolveResouceUri(fullUri), request, response);
        }


    }

    //Resolves Request handler to be used for requested uri
    static RequestHandler resolveRequestHandler(String requestType) {
        RequestHandler handler = handlers.get(requestType);
        return handler;
    }

    private static void registerRequestHandlers() {
        System.out.println("Resolving request handlers");
        try {

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().addUrls(RequestHandler.class.getProtectionDomain().getCodeSource().getLocation());
            configurationBuilder.addClassLoader(RequestHandler.class.getClassLoader());
            Set<Class<? extends RequestHandler>> requestHandlerClasses = new Reflections(configurationBuilder).getSubTypesOf(RequestHandler.class);

            for (Class<? extends RequestHandler> handlerClass : requestHandlerClasses) {
                String className = handlerClass.getSimpleName();
                RequestHandler handlerInstance = handlerClass.newInstance();
                System.out.println("Registering request handler: " + className);
                handlers.put(handlerInstance.getRequestType(), handlerClass.newInstance());
            }
        } catch (Exception e) {
            System.err.println("Failed initializing request handlers");
            e.printStackTrace();
        }

    }

}
