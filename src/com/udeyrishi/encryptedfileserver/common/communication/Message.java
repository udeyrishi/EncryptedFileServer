package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-30.
 */
public class Message {

    private final String message;

    public Message(String message) {
        this.message = message;
    }

    public String getTransmissionString() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
