package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.MessageBuilder;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolStandard {

    public static final int ENCRYPTION_KEY_BIT_COUNT = 256;
    private static final String FORMATTER_PATTERN = "type:%s;content:%s\n";


    public static class SpecialContent {
        public static final String NULL_CONTENT = "No-Content";
        public static final String NULL_ESCAPE = "\\";
    }

    public static class TypeNames {
        public static final String AUTH_REQUEST = "Auth-Request";
        public static final String AUTH_RESPONSE = "Auth-Response";

        public static final String FILE_REQUEST = "File-Request";
        public static final String FILE_RESPONSE_FAILURE = "File-Response-Failure";
        public static final String FILE_RESPONSE_SUCCESS = "File-Response-Success";

        public static final String TERMINATION_REQUEST = "Termination-Request";

        public static final String INTERRUPT_NOTIFICATION = "Interrupt";
    }

    public static class StandardMessages {
        public static final Message ACCESS_DENIED = MessageBuilder.responseMessage().addType(TypeNames.AUTH_RESPONSE)
                .addContent("Access-Denied").build();

        public static final Message ACCESS_GRANTED = MessageBuilder.responseMessage().addType(TypeNames.AUTH_RESPONSE)
                .addContent("Access-Granted").build();

        public static final Message BAD_FILE_REQUEST_RESPONSE = MessageBuilder.responseMessage()
                .addType(TypeNames.FILE_RESPONSE_FAILURE)
                .addContent("Bad-Request").build();

        public static final Message FILE_NOT_FOUND_RESPONSE = MessageBuilder.responseMessage()
                .addType(TypeNames.FILE_RESPONSE_FAILURE)
                .addContent("File-Not-Found").build();

        public static final Message TERMINATION_REQUEST = MessageBuilder.requestMessage()
                .addType(TypeNames.TERMINATION_REQUEST).build();

        public static Message authenticationRequest(String userID) {
            return MessageBuilder.requestMessage().addType(TypeNames.AUTH_REQUEST).addContent(userID).build();
        }

        public static Message fileRequest(String fileName) {
            return MessageBuilder.requestMessage().addType(TypeNames.FILE_REQUEST).addContent(fileName).build();
        }

        public static String serializedMessage(String type, String content) {
            return String.format(FORMATTER_PATTERN, type, content);
        }
    }
}
