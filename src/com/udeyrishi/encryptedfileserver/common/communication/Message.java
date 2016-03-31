package com.udeyrishi.encryptedfileserver.common.communication;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public interface Message {
    String getStringMessage() throws IOException;
}
