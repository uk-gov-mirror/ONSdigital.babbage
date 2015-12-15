package com.github.onsdigital.babbage.request;

import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.util.URIUtil;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Due to url design data, page, charts are all served through the same endpoint. The request type comes after resource uri
 * <p>
 * e.g. /economy/inflationpriceindices will renderTemplate serve cpi page, in order to get data /economy/inflationpriceindices/data is to be used
 * <p>
 * RequestDelegator resolves what type of GET request is made and delegates flow to appropriate handlers
 */
public class RequestDelegator {

    private static Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();

    //Find request handlers and register
    static {
        registerRequestHandlers();
    }

    public static void get(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        try {
            String uri = URIUtil.cleanUri(request.getRequestURI());
            String requestType = URIUtil.resolveRequestType(uri);
            RequestHandler handler = resolveRequestHandler(requestType);
            String requestedUri = uri;
            if (handler == null) {
                handler = handlers.get("/"); //default handler
            } else {
                //remove last segment to get requested resource uri
                requestedUri = com.github.onsdigital.babbage.util.URIUtil.removeLastSegment(uri);
            }
            handler.get(requestedUri, request).apply(request, response);
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
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
                if (!Modifier.isAbstract(handlerClass.getModifiers())) {
                    String className = handlerClass.getSimpleName();
                    RequestHandler handlerInstance = handlerClass.newInstance();
                    System.out.println("Registering request handler: " + className);
                    handlers.put(handlerInstance.getRequestType(), handlerInstance);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed initializing request handlers");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
