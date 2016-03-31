package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-30.
 */
public class CommunicationProtocol {
    private CommunicationProtocolState state;

    public CommunicationProtocol(CommunicationProtocolState initialState) {
        this.state = initialState;
    }

    public void processReceivedMessage(Message message) {
        state.messageReceived(this, message);
    }

    public Message getNextTransmissionMessage() {
        return state.nextTransmissionMessage(this);
    }

    public boolean isTerminated() {
        return state == null;
    }

    public void interrupt() {
        state.interrupt();
    }

    public void setState(CommunicationProtocolState state) {
        this.state = state;
    }

    public interface CommunicationProtocolState {
        void messageReceived(CommunicationProtocol protocol, Message message);
        Message nextTransmissionMessage(CommunicationProtocol protocol);
        void interrupt();
    }
}
