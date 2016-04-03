package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingResponseMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.tea.TEAMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.util.Map;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAAuthenticationState implements CommunicationProtocolState {
    private final Map<String, TEAKey> authenticationKeys;
    private final CommunicationProtocolState onAuthState;
    private TEAKey matchedKey = null;

    public TEAAuthenticationState(Map<String, TEAKey> authenticationKeys,
                                  CommunicationProtocolState onAuthState) {
        this.authenticationKeys = Preconditions.checkNotNull(authenticationKeys, "authenticationKeys");
        this.onAuthState = Preconditions.checkNotNull(onAuthState, "onAuthState");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException {
        // reset
        matchedKey = null;
        for (Map.Entry<String, TEAKey> key : authenticationKeys.entrySet()) {

            TEAMessageFilter filter = new TEAMessageFilter(key.getValue());
            IncomingMessage decryptedMessage = filter.filter(message);
            if (decryptedMessage.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_REQUEST) &&
                    decryptedMessage.getContent().equals(key.getKey())) {
                matchedKey = key.getValue();
                break;
            }
        }
    }

    @Override
    public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
        if (matchedKey == null) {
            return new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE,
                    TEAFileServerProtocolStandard.SpecialContent.ACCESS_DENIED);
        } else {
            TEAMessageFilter encryptionFilter = new TEAMessageFilter(matchedKey);

            OutgoingMessage encryptedAccessGrantedMessage = encryptionFilter.filter(
                    new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE,
                            TEAFileServerProtocolStandard.SpecialContent.ACCESS_GRANTED));

            // Change to file-transfer state and encrypt all messages from here onwards
            protocol.setIncomingMessageFilter(encryptionFilter);
            protocol.setOutgoingMessageFilter(encryptionFilter);
            protocol.setState(onAuthState);
            return encryptedAccessGrantedMessage;
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        protocol.setState(CommunicationProtocol.TERMINATED_STATE);
    }
}
