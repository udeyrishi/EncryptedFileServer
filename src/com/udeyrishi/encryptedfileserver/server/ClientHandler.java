package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
class ClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.createConsoleLogger(ClientHandler.class.getName());
    private final Socket socket;
    private final CommunicationProtocol protocol;

    ClientHandler(Socket socket, CommunicationProtocol protocol) {
        this.socket = Preconditions.checkNotNull(socket, "socket");
        this.protocol = Preconditions.checkNotNull(protocol, "protocol");
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            while (true) {
                // TODO: The shouldTerminate() thingie doesn't work. It won't send back an interrupt message ever
                try {
                    IncomingRequestMessage incomingRequestMessage = new IncomingRequestMessage(in);
                    protocol.processReceivedMessage(incomingRequestMessage);
                    logger.log(Level.FINEST, "Rx message processing completed");
                } catch (BadMessageException e) {
                    logger.log(Level.SEVERE, "Illegal message received from client. Ignoring and moving on.", e);
                }
                if (shouldTerminate()) {
                    break;
                }

                InputStream responseStream = protocol.getNextTransmissionMessage().getStream();
                StreamUtils.copyOverStreams(out, responseStream);
                responseStream.close();

                logger.log(Level.FINEST, "Tx message sent");
                if (shouldTerminate()) {
                    break;
                }
            }
        } catch (IOException | IllegalStateException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unhandled exception in ClientHandler", e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket after completing communications.", e);
        }

        logger.log(Level.FINER, "Shutting down client handler");
    }

    private boolean shouldTerminate() {
        if (Thread.currentThread().isInterrupted()) {
            protocol.interrupt();
        }

        return protocol.isTerminated();
    }
}
