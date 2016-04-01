package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.Message;

import java.io.IOException;

/**
 * Created by rishi on 2016-04-01.
 */
public class FileReceivalState implements CommunicationProtocolState {
    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) throws IOException, BadMessageException {

    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        return null;
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {

    }
}
