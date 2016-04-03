package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * Created by rishi on 2016-03-31.
 */
public class TEAAuthenticationState implements CommunicationProtocolState {
    private final String userID;
    private final CommunicationProtocolState nextState;

    public TEAAuthenticationState(String userID, CommunicationProtocolState nextState) {
        this.userID = Preconditions.checkNotNull(userID, "userID");
        this.nextState = Preconditions.checkNotNull(nextState, "nextState");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException {
        if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE) &&
                message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.ACCESS_GRANTED)) {
            protocol.setState(nextState);
        } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE) &&
                message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.ACCESS_DENIED)) {
            throw new AccessDeniedException("Invalid authentication userID-key combination");
        } else {
            throw new BadMessageException("Unknown message: " + message.getType() + ", " + message.getContent());
        }
    }

    @Override
    public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
        return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.AUTH_REQUEST, userID);
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        protocol.setState(CommunicationProtocol.TERMINATED_STATE);
    }
}
