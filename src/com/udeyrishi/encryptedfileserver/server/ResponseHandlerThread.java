package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;
import com.udeyrishi.encryptedfileserver.common.ThreadFinishedCallback;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
class ResponseHandlerThread extends Thread {

    private final Logger logger;
    private final Socket socket;
    private ThreadFinishedCallback threadFinishedCallback;

    ResponseHandlerThread(Socket socket, Logger logger, ThreadFinishedCallback threadFinishedCallback) {
        this.socket = Preconditions.checkNotNull(socket, "socket");
        this.logger = Preconditions.checkNotNull(logger, "logger");
        this.threadFinishedCallback = threadFinishedCallback;
    }

    @Override
    public void run() {

        // talk over the socket

        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket after completing communications.");
        }
        doOnFinishedCallback();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close socket after completing communications.");
        }
        // Stop transmission here
        doOnFinishedCallback();
    }

    private void doOnFinishedCallback() {
        if (threadFinishedCallback != null) {
            threadFinishedCallback.onThreadActionFinished(this);
            threadFinishedCallback = null;
        }
    }
}
