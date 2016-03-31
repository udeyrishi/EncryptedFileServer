package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-30.
 */
public class StringMessage implements Message {

    private final String message;

    public StringMessage(String message) {
        this.message = message;
    }

    public String getStringMessage() {
        return this.message;
    }
}
