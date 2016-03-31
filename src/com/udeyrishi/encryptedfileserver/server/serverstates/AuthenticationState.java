package com.udeyrishi.encryptedfileserver.server.serverstates;

import com.udeyrishi.encryptedfileserver.common.TEAKey;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;

import java.util.Map;

/**
 * Created by rishi on 2016-03-30.
 */
public class AuthenticationState implements CommunicationProtocol.CommunicationProtocolState {
    public AuthenticationState(Map<String, TEAKey> authenticationKeys) {

    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) {
        protocol.setState(null);
    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        return null;
    }

    @Override
    public void interrupt() {

    }
}
