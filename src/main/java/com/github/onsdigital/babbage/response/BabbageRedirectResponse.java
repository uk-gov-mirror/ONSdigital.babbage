package com.github.onsdigital.babbage.response;

import com.github.onsdigital.babbage.response.base.BabbageResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sends a redirect to the client
 */
public class BabbageRedirectResponse extends BabbageResponse {

    private String redirectUri;

    public BabbageRedirectResponse(String redirectAddress) {
        this.redirectUri = redirectAddress;
    }

    public BabbageRedirectResponse(String redirectAddress, Long maxAge) {
        this(redirectAddress);
        setMaxAge(maxAge);
    }

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCacheHeaders(request, response);

        String h1 = request.getHeader(HttpHeader.X_FORWARDED_HOST.asString());
        String h2 = request.getHeader(HttpHeader.X_FORWARDED_PROTO.asString());

        if ((null != h1 && !h1.isEmpty()) && (null != h2 && !h2.isEmpty())) {
            String url = buildHttpsRedirectUrl(h2, h1, redirectUri);
            System.out.println(String.format("Redirecting over https. URL=%s", url));
            response.sendRedirect(url);
        } else {
            System.out.println("Redirecting over http");
            response.sendRedirect(redirectUri);
        }
    }

    /**
     *
     * @param scheme
     * @param host
     * @param redirectUri
     * @return
     */
    private static String buildHttpsRedirectUrl(String scheme, String host, String redirectUri) {
        redirectUri = !redirectUri.startsWith("/") ? String.format("/%s", redirectUri) : redirectUri;
        String url = scheme + "://" + host + redirectUri;

        return url;
    }
}
