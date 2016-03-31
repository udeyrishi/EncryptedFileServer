package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.StringMessage;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolStandard {

    public static class TypeNames {
        public static final String AUTH_RESPONSE = "Auth-Response";
        public static final String AUTH_REQUEST = "Auth-Request";
        public static final String FILE_REQUEST = "File-Request";
        public static final String TERMINATION_REQUEST = "Termination-Request";
    }

    public static class StandardMessages {
        public static final Message ACCESS_DENIED = new StringMessage(TypeNames.AUTH_RESPONSE, "Access-Denied");
        public static final Message ACCESS_GRANTED = new StringMessage(TypeNames.AUTH_RESPONSE, "Access-Granted");
        public static final Message TERMINATION_REQUEST = new StringMessage(TypeNames.TERMINATION_REQUEST, null);
    }

    public static class MessageBuilder {
        public static Message authenticationRequest(String userID) {
            return new StringMessage(TypeNames.AUTH_REQUEST, userID);
        }

        public static Message fileRequest(String fileName) {
            return new StringMessage(TypeNames.FILE_REQUEST, fileName);
        }
    }
}
