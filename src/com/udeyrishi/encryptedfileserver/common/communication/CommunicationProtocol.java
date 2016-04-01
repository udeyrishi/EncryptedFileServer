package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-30.
 */
public class CommunicationProtocol {
    public static final CommunicationProtocolState TERMINATED_STATE = new CommunicationProtocolState() {
        @Override
        public void messageReceived(CommunicationProtocol protocol, Message message) throws IllegalStateException {
            throw new IllegalStateException("Can't receive messages when protocol is terminated.");
        }

        @Override
        public Message nextTransmissionMessage(CommunicationProtocol protocol) {
            throw new IllegalStateException("Can't transmit messages when protocol is terminated.");
        }

        @Override
        public void interrupt(CommunicationProtocol protocol) throws IllegalStateException {
            // no-op
        }
    };
    private static Logger logger = LoggerFactory.createConsoleLogger(CommunicationProtocol.class.getName());
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
        logger.log(Level.FINER, "Communication protocol's state changed to: " + state.getClass().getName());
    }

    public void setMessageFilter(MessageFilter filter) {
        this.filter = filter;
        logger.log(Level.FINER, String.format("Filter %s attached to communication protocol",
                filter == null ? "null" : filter.getClass().getName()));
    }

    public void processReceivedMessage(Message message) throws IllegalStateException, IOException, BadMessageException {
        message = filter == null ? message : filter.incomingMessageFilter(message);
        logger.log(Level.FINEST, "Message received");
        state.messageReceived(this, message);
    }

    public Message getNextTransmissionMessage() throws IllegalStateException {
        Message message = state.nextTransmissionMessage(this);
        message = filter == null ? message : filter.outgoingMessageFilter(message);
        logger.log(Level.FINEST, "Message transmitted");
        return message;
    }

    public boolean isTerminated() {
        return state == TERMINATED_STATE;
    }

    public void interrupt() {
        logger.log(Level.FINER, "Interrupted");
        state.interrupt(this);
    }
}
