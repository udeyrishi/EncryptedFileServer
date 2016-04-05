package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingResponseMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.CompoundIncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.CompoundOutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.OutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.tea.TEAMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.io.InputStream;
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
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message)
            throws IOException, BadMessageException {

        matchedKey = null;
        TEAMessageFilter encryptionFilter = null;
        IncomingMessageFilter defaultFilter = message.getFilter();

        for (Map.Entry<String, TEAKey> key : authenticationKeys.entrySet()) {

            if (encryptionFilter != null) {
                InputStream encryptedStream = encryptionFilter.getRawMessageCache();
                encryptionFilter.turnOffRawMessageCaching();
                message = new IncomingRequestMessage(encryptedStream);
                if (defaultFilter != null) {
                    message.setFilter(defaultFilter);
                }
            }

            encryptionFilter = new TEAMessageFilter(key.getValue());
            encryptionFilter.turnOnRawMessageCaching();

            if (message.getFilter() == null) {
                message.setFilter(encryptionFilter);
            } else {
                message.setFilter(new CompoundIncomingMessageFilter(encryptionFilter, message.getFilter()));
            }
            try {
                if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_REQUEST) &&
                        message.getContent().equals(key.getKey())) {
                    matchedKey = key.getValue();
                    break;
                }
            } catch (BadMessageException e) {
                // Because of decryption failure or garbage message. Ignore
            } catch (RuntimeException e) {
                if (e.getCause() == null || !(e.getCause() instanceof BadMessageException)) {
                    throw e;
                }
                // else ignore
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

            OutgoingMessage accessGrantedMessage
                    = new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE,
                    TEAFileServerProtocolStandard.SpecialContent.ACCESS_GRANTED);

            accessGrantedMessage.setFilter(encryptionFilter);

            // Change to file-transfer state and encrypt all messages from here onwards
            IncomingMessageFilter incomingMessageFilter;
            if (protocol.getIncomingMessageFilter() == null) {
                incomingMessageFilter = encryptionFilter;
            } else {
                incomingMessageFilter = new CompoundIncomingMessageFilter(encryptionFilter, protocol.getIncomingMessageFilter());
            }

            OutgoingMessageFilter outgoingMessageFilter;
            if (protocol.getOutgoingMessageFilter() == null) {
                outgoingMessageFilter = encryptionFilter;
            } else {
                outgoingMessageFilter = new CompoundOutgoingMessageFilter(protocol.getOutgoingMessageFilter(), encryptionFilter);
            }

            protocol.setIncomingMessageFilter(incomingMessageFilter);
            protocol.setOutgoingMessageFilter(outgoingMessageFilter);
            protocol.setState(onAuthState);
            return accessGrantedMessage;
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        protocol.setState(CommunicationProtocol.TERMINATED_STATE);
    }
}
