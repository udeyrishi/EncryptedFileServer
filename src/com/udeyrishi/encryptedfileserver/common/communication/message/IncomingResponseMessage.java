package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.utils.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class IncomingResponseMessage extends IncomingMessage {
    private boolean streamReadUntilAttachment = false;
    private String type = null;
    private String content = null;

    public IncomingResponseMessage(InputStream stream) {
        super(stream);
    }

    @Override
    public String getType() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return type;
    }

    @Override
    public String getContent() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return content;
    }

    @Override
    public InputStream getAttachmentStream() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return stream;
    }

    private void readMessageUntilAttachment() throws IOException, BadMessageException {
        if (streamReadUntilAttachment) {
            return;
        }

        Pair<String, String> typeAndContent = parseMessage(new String(readStreamUntilNewlineOrEOF()));

        this.type = typeAndContent.getFirst();
        this.content = typeAndContent.getSecond();
        streamReadUntilAttachment = true;
    }

    private byte[] readStreamUntilNewlineOrEOF() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (true) {
            byte lastByte = (byte) stream.read();

            if (lastByte == (byte) -1) {
                break;
            } else if (lastByte == (byte) '\n') {
                byteArrayOutputStream.write(lastByte);
                break;
            } else {
                byteArrayOutputStream.write(lastByte);
            }
        }

        return byteArrayOutputStream.toByteArray();
    }
}
