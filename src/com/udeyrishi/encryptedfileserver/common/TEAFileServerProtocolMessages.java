package com.udeyrishi.encryptedfileserver.common;

import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.StringMessage;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolMessages {
    public static final Message ACCESS_DENIED = new StringMessage("Access-Response", "Access-Denied");
    public static final Message ACCESS_GRANTED = new StringMessage("Access-Response", "Access-Granted");
}
