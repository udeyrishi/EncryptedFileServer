package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.MessageBuilder;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.*;
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
        try (OutputStream attachmentOutStream = socket.getOutputStream();
             PrintWriter messageStream = new PrintWriter(attachmentOutStream, true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                try {
                    Message received = MessageBuilder.requestMessage().addTypeAndContent(in).autoCloseReader(false).build();
                    protocol.processReceivedMessage(received);
                    logger.log(Level.FINEST, "Rx message processing completed");
                } catch (BadMessageException e) {
                    logger.log(Level.SEVERE, "Illegal message received from client. Ignoring and moving on.", e);
                }
                if (shouldTerminate()) {
                    break;
                }

                Message response = protocol.getNextTransmissionMessage();
                String messageContents = response.serializeMessage();
                messageStream.println(messageContents);

                InputStream attachmentInStream = response.getAttachmentStream();
                if (attachmentInStream != null) {
                    messageStream.println("\n");
                    sendAttachment(attachmentOutStream, attachmentInStream);
                    // attachmentInStream is use and throw
                    attachmentInStream.close();
                }

                logger.log(Level.FINEST, "Tx message sent");
                if (shouldTerminate()) {
                    break;
                }
            }
        } catch (IOException | IllegalStateException | BadMessageException e) {
            // Catching BadMessageException from Tx side is fatal, but should be resistant to garbage from Rx side
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

    private void sendAttachment(OutputStream attachmentOutStream, InputStream attachmentInStream) throws IOException {
        int count;
        byte[] buffer = new byte[8192];
        while ((count = attachmentInStream.read(buffer)) > 0) {
            attachmentOutStream.write(buffer, 0, count);
        }
        attachmentOutStream.flush();
    }

    private boolean shouldTerminate() {
        if (Thread.currentThread().isInterrupted()) {
            protocol.interrupt();
        }

        return protocol.isTerminated();
    }
}
