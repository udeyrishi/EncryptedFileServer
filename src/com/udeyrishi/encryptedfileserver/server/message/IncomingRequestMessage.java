package com.udeyrishi.encryptedfileserver.server.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.utils.Pair;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class IncomingRequestMessage extends IncomingMessage {

    private boolean streamRead = false;
    private String type = null;
    private String content = null;

    public IncomingRequestMessage(InputStream stream) {
        super(stream);
    }

    @Override
    public String getType() throws IOException, BadMessageException {
        readMessage();
        return type;
    }

    @Override
    public String getContent() throws IOException, BadMessageException {
        readMessage();
        return content;
    }

    private void readMessage() throws IOException, BadMessageException {
        if (streamRead) {
            return;
        }

        Pair<String, String> typeAndContent = parseMessage(new String(readFromStream()));
        this.type = typeAndContent.getFirst();
        this.content = typeAndContent.getSecond();

        streamRead = true;
    }



}
