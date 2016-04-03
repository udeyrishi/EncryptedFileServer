package com.udeyrishi.encryptedfileserver.common.communication.message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class OutgoingRequestMessage extends OutgoingMessage {

    public OutgoingRequestMessage(String type, String content) {
        super(type, content);
    }

    @Override
    public InputStream getStream() {
        final byte[] requestMessage = serializedMessage(getType(), getContent()).getBytes();

        return new InputStream() {
            private int i = 0;

            @Override
            public int read() throws IOException {
                if (i < requestMessage.length) {
                    return requestMessage[i++];
                } else {
                    return -1;
                }
            }
        };
    }
}
