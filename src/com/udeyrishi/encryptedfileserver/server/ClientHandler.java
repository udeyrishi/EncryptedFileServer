package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.communication.BufferedReaderMessage;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                protocol.processReceivedMessage(new BufferedReaderMessage(in));
                if (shouldTerminate()) {
                    break;
                }
                out.println(protocol.getNextTransmissionMessage().getStringMessage());
                if (shouldTerminate()) {
                    break;
                }
            }

        } catch (IOException | IllegalStateException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket after completing communications.", e);
        }
    }

    private boolean shouldTerminate() {
        if (Thread.currentThread().isInterrupted()) {
            protocol.interrupt();
        }

        return protocol.isTerminated();
    }
}
