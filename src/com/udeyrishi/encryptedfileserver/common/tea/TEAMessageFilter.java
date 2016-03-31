package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.MessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAMessageFilter implements MessageFilter {

    private final TEAKey key;

    //TODO: JNI based TEA encryption/decryption
    public TEAMessageFilter(TEAKey key) {
        this.key = Preconditions.checkNotNull(key, "key");
    }

    @Override
    public Message incomingMessageFilter(Message message) {
        return message;
    }

    @Override
    public Message outgoingMessageFilter(Message message) {
        return message;
    }
}
