package com.udeyrishi.encryptedfileserver.common.communication.message;

/**
 * Created by rishi on 2016-04-02.
 */
public interface OutgoingMessageFilter {
    OutgoingMessage filter(OutgoingMessage message);
}
