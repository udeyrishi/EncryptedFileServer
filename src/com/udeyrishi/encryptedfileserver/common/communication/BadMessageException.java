package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-30.
 */
public class BadMessageException extends Exception {
    public BadMessageException(String rawMessage) {
        super("Bad format message received: " + rawMessage);
    }
}
