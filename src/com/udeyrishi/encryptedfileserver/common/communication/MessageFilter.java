package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-31.
 */
public interface MessageFilter {
    Message incomingMessageFilter(Message message);

    Message outgoingMessageFilter(Message message);
}
