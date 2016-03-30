package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
class ResponseHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ResponseHandler.class.getName());
    private final Socket socket;

    ResponseHandler(Socket socket) {
        this.socket = Preconditions.checkNotNull(socket, "socket");
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            // talk over the socket
        }

        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket after completing communications.");
        }
    }
}
