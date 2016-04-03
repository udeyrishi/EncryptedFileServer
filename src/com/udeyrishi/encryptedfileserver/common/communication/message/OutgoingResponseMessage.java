package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class OutgoingResponseMessage extends OutgoingMessage {
    private final InputStream attachmentStream;
    private final byte[] attachmentSize;

    public OutgoingResponseMessage(String type, String content) {
        this(type, content, null, 0);
    }

    public OutgoingResponseMessage(String type, String content, InputStream attachmentStream, long attachmentSize) {
        super(type, content);
        this.attachmentStream = attachmentStream;
        this.attachmentSize = ByteUtils.longToBytes(attachmentSize);
    }

    @Override
    protected InputStream getRawStream() {
        final byte[] firstLine = serializedMessage(getType(), getContent()).getBytes();

        return new InputStream() {
            private int firstLineIndex = 0;
            private int attachmentSizeIndex = 0;

            @Override
            public int read() throws IOException {
                if (firstLineIndex < firstLine.length) {
                    return firstLine[firstLineIndex++];
                } else if (attachmentSizeIndex < attachmentSize.length) {
                    return attachmentSize[attachmentSizeIndex++];
                } else if (attachmentStream == null) {
                    return -1;
                } else {
                    return attachmentStream.read();
                }
            }

            @Override
            public void close() throws IOException {
                super.close();
                if (attachmentStream != null) {
                    attachmentStream.close();
                }
            }
        };
    }
}
