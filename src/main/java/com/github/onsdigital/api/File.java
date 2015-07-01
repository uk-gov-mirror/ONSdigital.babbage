package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.api.util.ApiErrorHandler;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.data.DataNotFoundException;
import com.github.onsdigital.data.zebedee.ZebedeeClient;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.data.zebedee.ZebedeeUtil;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.request.response.BabbageBinaryResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.Files;

/**
 * Created by bren on 01/07/15.
 * <p/>
 * Starts download for requested file in content directory
 */
@Api
public class File {

    @GET
    public Object post(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        ZebedeeClient zebedeeClient = null;
        try {
            String uri = request.getParameter("uri");
            if (StringUtils.isEmpty(uri)) {
                throw new IllegalArgumentException("File uri not supplied");
            }

            InputStream fileStream = null;
            ZebedeeRequest zebedeeRequest = ZebedeeUtil.getZebedeeRequest(uri, request.getCookies());
            if (zebedeeRequest == null) {
                Path file = getFile(uri);
                fileStream = Files.newInputStream(file);
            } else {
                zebedeeClient = new ZebedeeClient(zebedeeRequest);
                fileStream = getFile(uri, zebedeeClient);
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + FilenameUtils.getName(uri) + "\"");
            new BabbageBinaryResponse(fileStream, FilenameUtils.getExtension(uri)).apply(response);
        } catch (Exception e) {
            ApiErrorHandler.handle(e, response);
        } finally {
            if (zebedeeClient != null) {
                zebedeeClient.closeConnection();
            }
        }
        return null;
    }

    private Path getFile(String uriString)
            throws IOException {
        // Standardise the path:
        String uriPath = StringUtils.removeStart(uriString, "/");
        System.out.println("Reading file under" + uriPath);
        Path path = FileSystems.getDefault().getPath(
                Configuration.getContentPath());

        Path data = path.resolve(uriPath);
        if (!java.nio.file.Files.exists(data)) {
            throw new ResourceNotFoundException();
        }
        return data;
    }

    private InputStream getFile(String uriString, ZebedeeClient client)
            throws IOException, ContentNotFoundException {
        return  client.get("file", uriString, false);
    }


}
