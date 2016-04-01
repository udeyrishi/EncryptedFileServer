package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * Created by rishi on 2016-03-31.
 */
public class TEAAuthenticationState implements CommunicationProtocolState {
    private final String userID;

    public TEAAuthenticationState(String userID) {
        this.userID = Preconditions.checkNotNull(userID, "userID");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) throws IOException, BadMessageException {
        if (message.isEqualTo(TEAFileServerProtocolStandard.StandardMessages.ACCESS_GRANTED)) {
            protocol.setState(new FileReceivalState());
        } else if (message.isEqualTo(TEAFileServerProtocolStandard.StandardMessages.ACCESS_DENIED)) {
            throw new AccessDeniedException("Invalid authentication userID-key combination");
        } else {
            throw new BadMessageException(message.serializeMessage());
        }
    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        return TEAFileServerProtocolStandard.StandardMessages.authenticationRequest(userID);
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        protocol.setState(CommunicationProtocol.TERMINATED_STATE);
    }
}
