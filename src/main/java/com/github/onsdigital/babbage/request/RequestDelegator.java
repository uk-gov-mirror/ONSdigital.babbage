package com.github.onsdigital.babbage.request;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.URIUtil;
import com.github.onsdigital.cache.BabbageResponseCache;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.request.response.BabbageResponse;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.github.onsdigital.configuration.Configuration.GENERAL.isCacheEnabled;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Due to url design data, page, charts are all served through the same endpoint. The request type comes after resource uri
 * <p/>
 * e.g. /economy/inflationpriceindices will render serve cpi page, in order to get data /economy/inflationpriceindices/data is to be used
 * <p/>
 * RequestDelegator resolves what type of GET request is made and delegates flow to appropriate handlers
 */
public class RequestDelegator {

    private static Map<String, RequestHandler> handlers = new HashMap<String, RequestHandler>();


    //Using a common cache for all request types for now, data requests, image requests , latest data requests, chart requests are all cached in a single cache with uri's as keys
    private static BabbageResponseCache responseCache = new BabbageResponseCache(Configuration.GENERAL.getGlobalRequestCacheSize());


    //Find request handlers and register
    static {
        registerRequestHandlers();
    }

    public static void get(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        try {
            RequestUtil.saveRequestContext(request);

            String uri = URIUtil.cleanUri(request.getRequestURI());
            String fullUri = uri + "?" + StringUtils.lowerCase(request.getQueryString());
            String requestType = URIUtil.resolveRequestType(uri);
            RequestHandler handler = resolveRequestHandler(requestType);
            BabbageResponse getResponse = null;
            String requestedUri = uri;
            if (handler == null) {
                handler = handlers.get("/"); //default handler
            } else {
                //remove last segment to get requested resource uri
                requestedUri = com.github.onsdigital.babbage.util.URIUtil.resolveResouceUri(uri);
            }

            getResponse = get(fullUri, requestedUri, request, handler);

            //tell client not to ask again for 5 mins //todo: caching should be based on cache-headers from content service
            if (isCacheEnabled()) {
                response.addHeader("cache-control", "public, max-age=300");
            }

            getResponse.apply(response);
            return;
        } finally {
            RequestUtil.clearContext();
        }
    }

    private static BabbageResponse get(final String fullUri, final String requestedUri, final HttpServletRequest request, final RequestHandler handler) throws Throwable {

        //No caching on development
        if (!isCacheEnabled()) {
            return handler.get(requestedUri, request);
        }
        try {
            //caching resource with will uri as key
            return responseCache.get(fullUri, new Callable<BabbageResponse>() {
                @Override
                public BabbageResponse call() throws Exception {
                    System.out.println("Cache miss for " + fullUri + " loading ");
                    return handler.get(requestedUri, request);
                }
            });
        } catch (ExecutionException e) {
            throw e.getCause();
        } catch (UncheckedExecutionException e) {
            throw e.getCause();
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
