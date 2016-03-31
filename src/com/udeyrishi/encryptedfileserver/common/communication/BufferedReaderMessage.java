package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public class BufferedReaderMessage implements Message {
    private final BufferedReader reader;

    private String messageContent = null;
    private String typeName = null;

    public BufferedReaderMessage(BufferedReader reader) {
        this.reader = Preconditions.checkNotNull(reader, "reader");
    }

    @Override
    public String getTypeName() throws IOException, BadMessageException {
        if (typeName == null) {
            readMessage();
        }
        return typeName;
    }

    @Override
    public String getMessageContents() throws IOException, BadMessageException {
        if (messageContent == null) {
            readMessage();
        }
        return messageContent;
    }

    private void readMessage() throws IOException, BadMessageException {
        Message message = new MessageParser().stringToMessage(reader.readLine());
        this.messageContent = message.getMessageContents();
        this.typeName = message.getTypeName();
    }
}
