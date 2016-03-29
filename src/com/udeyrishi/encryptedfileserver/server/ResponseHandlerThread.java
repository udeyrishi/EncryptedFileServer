package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;
import com.udeyrishi.encryptedfileserver.common.ThreadFinishedCallback;

import java.net.Socket;
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
        doOnCompleteCallback();
    }

    @Override
    public void interrupt() {
        super.interrupt();

        // Stop transmission here
        doOnCompleteCallback();
    }

    private void doOnCompleteCallback() {
        if (threadFinishedCallback != null) {
            threadFinishedCallback.onComplete(this);
            threadFinishedCallback = null;
        }
    }
}
