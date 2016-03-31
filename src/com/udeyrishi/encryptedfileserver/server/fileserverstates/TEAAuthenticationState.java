package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.TEAFileServerProtocolMessages;
import com.udeyrishi.encryptedfileserver.common.tea.TEAMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;

import java.util.Map;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAAuthenticationState implements CommunicationProtocol.CommunicationProtocolState {
    private final Map<String, TEAKey> authenticationKeys;
    private final CommunicationProtocol.CommunicationProtocolState onAuthState;
    private TEAKey matchedKey = null;

    public TEAAuthenticationState(Map<String, TEAKey> authenticationKeys,
                                  CommunicationProtocol.CommunicationProtocolState onAuthState) {
        this.authenticationKeys = Preconditions.checkNotNull(authenticationKeys, "authenticationKeys");
        this.onAuthState = Preconditions.checkNotNull(onAuthState, "onAuthState");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) {
        // reset
        matchedKey = null;
        for (Map.Entry<String, TEAKey> key : authenticationKeys.entrySet()) {
            TEAMessageFilter filter = new TEAMessageFilter(key.getValue());
            Message decryptedMessage = filter.incomingMessageFilter(message);
            if (decryptedMessage.toString().equals(key.getKey())) {
                matchedKey = key.getValue();
                break;
            }
        }
    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        if (matchedKey == null) {
            return TEAFileServerProtocolMessages.ACCESS_DENIED;
        } else {
            TEAMessageFilter encryptionFilter = new TEAMessageFilter(matchedKey);
            Message encryptedAccessGrantedMessage
                    = encryptionFilter.outgoingMessageFilter(TEAFileServerProtocolMessages.ACCESS_GRANTED);

            // Change to file-transfer state and encrypt all messages from here onwards
            protocol.setMessageFilter(encryptionFilter);
            protocol.setState(onAuthState);
            return encryptedAccessGrantedMessage;
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        protocol.setState(CommunicationProtocol.TERMINATED_STATE);
    }
}
