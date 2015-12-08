package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.error.BabbageException;
import com.github.onsdigital.babbage.response.BabbageStringResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * Created by bren on 16/11/15.
 *
 * Temporary end point for getting hash value of requested resource uri,
 *
 * TODO:// Implemented verification using proper etag http headers
 */
@Api
public class Hash {
    @GET

    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        try (ContentStream contentStream = ContentClient.getInstance().getResource(request.getParameter("uri"))) {
            String hash = Hex.encodeHexString(DigestUtils.sha1(contentStream.getDataStream()));
            new BabbageStringResponse(hash, contentStream.getMimeType()).apply(response);
            return;
        } catch (ContentReadException e) {
            writeError(response, e.getStatusCode());
        } catch (BabbageException e){
            writeError(response, e.getStatusCode());
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
        }
    }

    void writeError(@Context HttpServletResponse response, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(TEXT_PLAIN);
        IOUtils.write("Failed reading content!", response.getOutputStream());
    }
}
