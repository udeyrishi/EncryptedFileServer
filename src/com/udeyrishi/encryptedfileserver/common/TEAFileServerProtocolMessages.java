package com.udeyrishi.encryptedfileserver.common;

import com.udeyrishi.encryptedfileserver.common.communication.Message;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolMessages {
    public static final Message ACCESS_DENIED = new Message("Access-Denied");
    public static final Message ACCESS_GRANTED = new Message("Access-Granted");

}
