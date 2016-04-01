package com.udeyrishi.encryptedfileserver.common.communication;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-31.
 */
public interface CommunicationProtocolState {
    void messageReceived(CommunicationProtocol protocol, Message message) throws IOException, BadMessageException;

    Message nextTransmissionMessage(CommunicationProtocol protocol);

    void interrupt(CommunicationProtocol protocol);
}