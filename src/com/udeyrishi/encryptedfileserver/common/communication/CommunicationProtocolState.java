package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-31.
 */
public interface CommunicationProtocolState {
    void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException;

    OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol);

    void interrupt(CommunicationProtocol protocol);
}