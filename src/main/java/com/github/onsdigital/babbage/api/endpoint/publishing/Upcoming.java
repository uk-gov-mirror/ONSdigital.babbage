package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.davidcarboni.cryptolite.Password;
import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.error.BabbageException;
import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.ContentDetail;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;
import com.github.onsdigital.babbage.publishing.model.ResponseMessage;
import com.github.onsdigital.babbage.util.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.io.IOException;
import java.util.List;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;

/**
 * Created by bren on 16/12/15.
 */
@Api
public class Upcoming {

    private boolean verifyUriList = true;

    //Using same key as reindex
    private static final String REINDEX_KEY_HASH = "5NpB6/uAgk14nYwHzMbIQRnuI2W63MrBOS2279YlcUUY2kNOhrL+R5UFR3O066bQ";

    public Upcoming() {

    }

    public Upcoming(boolean verifyUriList) {
        this.verifyUriList = verifyUriList;
    }


    @POST
    public Object post(HttpServletRequest request, HttpServletResponse response) {
        try {
            PublishNotification publishNotification = JsonUtil.fromJson(request.getInputStream(), PublishNotification.class);
            return process(response, publishNotification);
        } catch (BabbageException e) {
            response.setStatus(e.getStatusCode());
            return new ResponseMessage(e.getMessage());
        } catch (Exception e) {
            error().exception(e).log("api Upcoming: error handling POST request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ResponseMessage("Failed processing uri list publish dates");
        }
    }

    protected Object process(HttpServletResponse response, PublishNotification publishNotification) throws IOException {
        verifyKey(publishNotification);
        if (verifyUriList) {
            verifyUriList(publishNotification);
        }
        notifyPublishEvent(publishNotification);
        response.setStatus(HttpServletResponse.SC_OK);
        return new ResponseMessage("Successfully processed");
    }

    private void verifyUriList(PublishNotification publishNotification) {
        List<String> urisToUpdate = publishNotification.getUrisToUpdate();
        List<ContentDetail> urisToDelete = publishNotification.getUrisToDelete();

        if ((urisToUpdate == null || urisToUpdate.isEmpty())
                && (urisToDelete == null || urisToDelete.isEmpty())) {
            throw new BadRequestException("Please speficy uri list");
        }
    }

    private void verifyKey(PublishNotification notification) {
        if (!Password.verify(notification.getKey(), REINDEX_KEY_HASH)) {
            throw new BadRequestException("Wrong key, make sure you pass in the right key");
        }
    }

    protected void notifyPublishEvent(PublishNotification publishNotification) throws IOException {
        PublishingManager.getInstance().notifyUpcoming(publishNotification);
    }

}
