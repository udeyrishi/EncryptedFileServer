package com.udeyrishi.encryptedfileserver.server.message;

import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class OutgoingResponseMessage extends OutgoingMessage {
    private static final String FORMATTER_PATTERN = "type:%s;content:%s\n";
    private final InputStream attachmentStream;

    public OutgoingResponseMessage(String type, String content) {
        this(type, content, null);
    }

    public OutgoingResponseMessage(String type, String content, InputStream attachmentStream) {
        super(type, content);
        this.attachmentStream = attachmentStream;
    }

    @Override
    public InputStream getStream() {
        final byte[] firstLine = String.format(FORMATTER_PATTERN, getType(), getContent()).getBytes();

        return new InputStream() {
            int firstLineIndex = 0;
            
            @Override
            public int read() throws IOException {
                if (firstLineIndex < firstLine.length) {
                    return firstLine[firstLineIndex++];
                } else if (attachmentStream == null) {
                    return -1;
                } else {
                    return attachmentStream.read();
                }
            }
        };
    }
}
