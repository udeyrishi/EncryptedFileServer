package com.udeyrishi.encryptedfileserver.common.communication.message;

import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public interface OutgoingMessageFilter {
    InputStream filterOutgoingMessage(InputStream message);
}
