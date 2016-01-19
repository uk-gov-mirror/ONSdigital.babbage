package com.github.onsdigital.babbage.request.handler.content;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;

import javax.servlet.http.HttpServletRequest;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Handle data requests. Proxies data requests to content service
 */
public class DataRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "data";
//    private static Map<String, ListPageBaseRequestHandler> listPageHandlers = new HashMap<>();


    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return getData(requestedUri, request);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    public static String requestType() {
        return REQUEST_TYPE;
    }

    public BabbageResponse getData(String uri, HttpServletRequest request) throws Exception {

//        String requestType = URIUtil.resolveRequestType(uri);

//        if (listPageHandlers.containsKey(requestType)) {
//            return listPageHandlers.get(requestType).getData(URIUtil.removeLastSegment(uri), request);
//        }

        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri, getQueryParameters(request));
        return new BabbageContentBasedStringResponse(contentResponse, contentResponse.getAsString());
    }

    static {
//        registerListHandlers();
    }

//    private synchronized static void registerListHandlers() {
//        System.out.println("Resolving request handlers");
//        try {
//
//            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().addUrls(RequestHandler.class.getProtectionDomain().getCodeSource().getLocation());
//            configurationBuilder.addClassLoader(RequestHandler.class.getClassLoader());
//            Set<Class<? extends ListPageBaseRequestHandler>> requestHandlerClasses = new Reflections(configurationBuilder).getSubTypesOf(ListPageBaseRequestHandler.class);
//
//            for (Class<? extends ListPageBaseRequestHandler> handlerClass : requestHandlerClasses) {
//                if (!Modifier.isAbstract(handlerClass.getModifiers())) {
//                    String className = handlerClass.getSimpleName();
//                    ListPageBaseRequestHandler handlerInstance = handlerClass.newInstance();
//                    System.out.println("Registering ListPageBaseRequestHandler: " + className);
//                    listPageHandlers.put(handlerInstance.getRequestType(), handlerInstance);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Failed initializing request handlers");
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
}
