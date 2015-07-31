package com.github.onsdigital.request;

import com.github.onsdigital.cache.BabbageResponseCache;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.util.URIUtil;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.data.zebedee.ZebedeeUtil;
import com.github.onsdigital.request.handler.base.RequestHandler;
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
    private static BabbageResponseCache responseCache = new BabbageResponseCache(Configuration.getGlobalRequestCacheSize());


    //Find request handlers and register
    static {
        registerRequestHandlers();
    }

    public static void get(HttpServletRequest request, HttpServletResponse response) throws Throwable {

        String fullUri = URIUtil.cleanUri(request.getRequestURI());
        String uriWithParams = fullUri + "?" + StringUtils.lowerCase(request.getQueryString());
        String requestType = URIUtil.resolveRequestType(fullUri);
        RequestHandler handler = resolveRequestHandler(requestType);
        BabbageResponse getResponse = null;
        String requestedUri = fullUri;
        if (handler == null) {
            handler = handlers.get("/"); //default handler
        } else {
            requestedUri = URIUtil.resolveResouceUri(fullUri);
        }

        ZebedeeRequest zebedeeRequest = ZebedeeUtil.getZebedeeRequest(requestedUri, request.getCookies());
        getResponse = get(zebedeeRequest, uriWithParams,requestedUri, request, handler);

        //tell client not to ask again for 5 mins
        if(zebedeeRequest == null && Configuration.isDevelopment() == false) {
            response.addHeader("cache-control", "public, max-age=300");
        }

        getResponse.apply(response);
        return;
    }

    private static BabbageResponse get(ZebedeeRequest zebedeeRequest, final String fullUri, final String requestedUri, final HttpServletRequest request, final RequestHandler handler) throws Throwable {

        if (zebedeeRequest == null) {
            //No caching on development
            if (Configuration.isDevelopment()) {
                System.out.println("On development environment, not caching");
                return handler.get(requestedUri, request);
            }
            try {
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
