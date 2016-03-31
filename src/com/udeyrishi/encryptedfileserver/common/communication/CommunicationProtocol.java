package com.udeyrishi.encryptedfileserver.common.communication;

/**
 * Created by rishi on 2016-03-30.
 */
public class CommunicationProtocol {
    private CommunicationProtocolState state;
    private MessageFilter filter;

    public CommunicationProtocol(CommunicationProtocolState initialState, MessageFilter filter) {
        setState(initialState);
        setMessageFilter(filter);
    }

    public CommunicationProtocol(CommunicationProtocolState initialState) {
        this(initialState, null);
    }

    public void setState(CommunicationProtocolState state) {
        this.state = state;
    }

    public void setMessageFilter(MessageFilter filter) {
        this.filter = filter;
    }

    public void processReceivedMessage(Message message) {
        message = filter == null ? message : filter.incomingMessageFilter(message);
        state.messageReceived(this, message);
    }

    public Message getNextTransmissionMessage() {
        Message message = state.nextTransmissionMessage(this);
        message = filter == null ? message : filter.outgoingMessageFilter(message);
        return message;
    }

    public boolean isTerminated() {
        return state == TERMINATED_STATE;
    }

    public void interrupt() {
        state.interrupt(this);
    }

    public interface CommunicationProtocolState {
        void messageReceived(CommunicationProtocol protocol, Message message);
        Message nextTransmissionMessage(CommunicationProtocol protocol);
        void interrupt(CommunicationProtocol protocol);
    }

    public static final CommunicationProtocolState TERMINATED_STATE = new CommunicationProtocolState() {
        @Override
        public void messageReceived(CommunicationProtocol protocol, Message message) {
            // no-op
        }

        @Override
        public Message nextTransmissionMessage(CommunicationProtocol protocol) {
            // Should never be called if terminated. Try to throw NullPointerException somewhere
            return null;
        }

        @Override
        public void interrupt(CommunicationProtocol protocol) {
            // no-op
        }
    };

    public interface MessageFilter {
        Message incomingMessageFilter(Message message);
        Message outgoingMessageFilter(Message message);
    }
}
