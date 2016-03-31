package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.StringMessage;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolMessages {
    public static final Message ACCESS_DENIED = new StringMessage("Auth-Response", "Access-Denied");
    public static final Message ACCESS_GRANTED = new StringMessage("Auth-Response", "Access-Granted");

    public static Message createAuthenticationRequestMessage(String userID) {
        return new StringMessage("Auth-Requested", userID);
    }
}
