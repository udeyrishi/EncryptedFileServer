/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

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
import java.security.AccessControlException;

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
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message)
            throws IOException, AccessControlException {
        try {
            if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE) &&
                    message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.ACCESS_GRANTED)) {
                protocol.setState(nextState);
            } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.AUTH_RESPONSE) &&
                    message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.ACCESS_DENIED)) {
                throw new AccessControlException("Invalid authentication userID-key combination");
            } else {
                throw new BadMessageException("Unknown message: " + message.getType() + ", " + message.getContent());
            }
        } catch (BadMessageException e) {
            throw new AccessControlException("Authentication request rejected by the server");
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
