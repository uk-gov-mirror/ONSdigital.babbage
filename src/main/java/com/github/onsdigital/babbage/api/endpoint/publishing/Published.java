package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;

import java.io.IOException;

/**
 * Created by bren on 16/12/15.
 */
public class Published extends Upcoming {

    public Published() {
        super(false);
    }


    @Override
    protected void notifyPublishEvent(PublishNotification publishNotification) throws IOException {
        PublishingManager.getInstance().notifyPublished(publishNotification);
    }

}
