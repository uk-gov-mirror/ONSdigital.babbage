package com.github.onsdigital.request;

import com.github.onsdigital.api.util.URIUtil;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.data.zebedee.ZebedeeUtil;
import com.github.onsdigital.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
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
 * Due to url design data, page, charts are all served through the same endpoint. The request type comes after resource uri
 * <p>
 * e.g. /economy/inflationpriceindices will render serve cpi page, in order to get data /economy/inflationpriceindices/data is to be used
 * <p>
 * RequestDelegator resolves what type of GET request is made and delegates flow to appropriate handlers
 */
public class RequestDelegator {

    private static Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();

    //Find request handlers and register
    static {
        registerRequestHandlers();
    }

    public static void get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String fullUri = URIUtil.cleanUri(request.getRequestURI());
        String requestType = URIUtil.resolveRequestType(fullUri);
        RequestHandler handler = resolveRequestHandler(requestType);
        BabbageResponse getResponse = null;
        if (handler == null) {
            handler = handlers.get("/"); //default handler
            getResponse = get(fullUri, request, handler);
        } else {
            getResponse = get(URIUtil.resolveResouceUri(fullUri), request, handler);
        }


        //        if (!HostHelper.isLocalhost(request)) {
//            response.addHeader("cache-control", "public, max-age=300");
//        }

        response.setStatus(HttpServletResponse.SC_OK);
        getResponse.apply(response);
        return;
    }


    private static BabbageResponse get( String requestedUri,  HttpServletRequest request,  RequestHandler handler) throws Exception {

        ZebedeeRequest zebedeeRequest = ZebedeeUtil.getZebedeeRequest(requestedUri, request.getCookies());
        if (zebedeeRequest == null) {
            return handler.get(requestedUri, request);
        } else {
            return handler.get(requestedUri, request, zebedeeRequest);
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
                handlers.put(handlerInstance.getRequestType(), handlerInstance);
            }
        } catch (Exception e) {
            System.err.println("Failed initializing request handlers");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
