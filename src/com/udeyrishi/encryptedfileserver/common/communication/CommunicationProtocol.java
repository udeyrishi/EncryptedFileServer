package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.OutgoingMessageFilter;
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
        public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IllegalStateException {
            throw new IllegalStateException("Can't receive messages when protocol is terminated.");
        }

        @Override
        public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
            throw new IllegalStateException("Can't transmit messages when protocol is terminated.");
        }

        @Override
        public void interrupt(CommunicationProtocol protocol) throws IllegalStateException {
            // no-op
        }
    };
    private static Logger logger = LoggerFactory.createConsoleLogger(CommunicationProtocol.class.getName());
    private CommunicationProtocolState state;
    private IncomingMessageFilter incomingMessageFilter;
    private OutgoingMessageFilter outgoingMessageFilter;

    public CommunicationProtocol(CommunicationProtocolState initialState, IncomingMessageFilter incomingMessageFilter,
                                 OutgoingMessageFilter outgoingMessageFilter) {
        setState(initialState);
        setIncomingMessageFilter(incomingMessageFilter);
        setOutgoingMessageFilter(outgoingMessageFilter);
    }

    public CommunicationProtocol(CommunicationProtocolState initialState) {
        this(initialState, null, null);
    }

    public void setState(CommunicationProtocolState state) {
        this.state = state;
        logger.log(Level.FINER, "Communication protocol's state changed to: " + state.getClass().getName());
    }

    public void setIncomingMessageFilter(IncomingMessageFilter filter) {
        this.incomingMessageFilter = filter;
        logger.log(Level.FINER, String.format("Incoming Filter %s attached to communication protocol",
                filter == null ? "null" : filter.getClass().getName()));
    }

    public IncomingMessageFilter getIncomingMessageFilter() {
        return this.incomingMessageFilter;
    }

    public void setOutgoingMessageFilter(OutgoingMessageFilter filter) {
        this.outgoingMessageFilter = filter;
        logger.log(Level.FINER, String.format("Outgoing Filter %s attached to communication protocol",
                filter == null ? "null" : filter.getClass().getName()));
    }

    public OutgoingMessageFilter getOutgoingMessageFilter() {
        return this.outgoingMessageFilter;
    }

    public void processReceivedMessage(IncomingMessage message)
            throws IllegalStateException, IOException, BadMessageException {
        message.setFilter(incomingMessageFilter);
        logger.log(Level.FINEST, "Message received");
        state.messageReceived(this, message);
    }

    public OutgoingMessage getNextTransmissionMessage() throws IllegalStateException {
        OutgoingMessage message = state.nextTransmissionMessage(this);
        message.setFilter(outgoingMessageFilter);
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
