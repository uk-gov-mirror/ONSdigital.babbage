package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;

/**
 * Created by bren on 16/12/15.
 */
public class PublishCancelled extends Upcoming {

    public PublishCancelled() {
        super(false);
    }

    @Override
    protected void notifyPublishEvent(PublishNotification publishNotification) {
        PublishingManager.getInstance().notifyPublishCancel(publishNotification);
    }
}
