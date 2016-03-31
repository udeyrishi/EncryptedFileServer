package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public class StringMessage implements Message {

    private final String messageContents;
    private final String typeName;

    public StringMessage(String typeName, String messageContents) {
        this.typeName = Preconditions.checkNotNull(typeName, "typeName");
        this.messageContents = messageContents;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getMessageContents() throws IOException {
        return messageContents;
    }
}
