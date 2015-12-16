package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.davidcarboni.cryptolite.Password;
import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.error.BabbageException;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.ResponseMessage;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.util.List;

/**
 * Created by bren on 16/12/15.
 */
@Api
public class Upcoming {

    //Using same key as reindex
    private static final String REINDEX_KEY_HASH = "5NpB6/uAgk14nYwHzMbIQRnuI2W63MrBOS2279YlcUUY2kNOhrL+R5UFR3O066bQ";

    @POST
    public Object post(HttpServletRequest request, HttpServletResponse response, PublishNotification publishNotification) {
        try {
            verifyKey(publishNotification);
            verifyUriList(publishNotification);
            notifyPublishEvent(publishNotification);
            response.setStatus(HttpServletResponse.SC_OK);
            return new ResponseMessage("Successfully processed");
        } catch (BabbageException e) {
            response.setStatus(e.getStatusCode());
            return new ResponseMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ResponseMessage("Failed processing uri list publish dates");
        }
    }

    private void verifyUriList(PublishNotification publishNotification) {
        List<String> uriList = publishNotification.getUriList();
        if (uriList == null || uriList.isEmpty()) {
            throw new BadRequestException("Please speficy uri list");
        }
    }

    private void verifyKey(PublishNotification notification) {
        if (!Password.verify(notification.getKey(), REINDEX_KEY_HASH)) {
            throw new BadRequestException("Wrong key, make sure you pass in the right key");
        }
    }

    protected void notifyPublishEvent(PublishNotification publishNotification) {
        PublishingManager.getInstance().notifyUpcoming(publishNotification);
    }

}
