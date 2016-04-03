package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAMessageFilter implements IncomingMessageFilter, OutgoingMessageFilter {

    private final TEAKey key;

    //TODO: JNI based TEA encryption/decryption
    public TEAMessageFilter(TEAKey key) {
        this.key = Preconditions.checkNotNull(key, "key");
    }


    @Override
    public IncomingMessage filter(IncomingMessage message) {
        return message;
    }

    @Override
    public OutgoingMessage filter(OutgoingMessage message) {
        return message;
    }
}
