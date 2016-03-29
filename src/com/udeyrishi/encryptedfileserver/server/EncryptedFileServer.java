package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;
import com.udeyrishi.encryptedfileserver.common.TEAKey;
import com.udeyrishi.encryptedfileserver.common.ThreadFinishedCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class EncryptedFileServer implements MultiThreadedServer {
    private final Integer port;
    private final Map<String, TEAKey> keys;
    private final Logger logger;
    private boolean isGracefulShutDownRequested = false;

    // Access this list safely using "synchronised" as ResponseHandlerThread callbacks might touch it
    private final List<ResponseHandlerThread> activeResponseHandlers = new LinkedList<>();

    EncryptedFileServer(Integer port, Map<String, TEAKey> keys, Logger logger) {
        this.port = Preconditions.checkNotNull(port, "port");
        this.keys = Preconditions.checkNotNull(keys, "keys");
        this.logger = Preconditions.checkNotNull(logger, "logger");
    }

    @Override
    public void run() {
        ServerSocket serverSocket = getServerSocket();
        if (serverSocket == null) {
            return;
        }

        while (!isGracefulShutDownRequested) {
            Socket socket = accept(serverSocket);
            if (socket != null) {
                ResponseHandlerThread responseHandlerThread = new ResponseHandlerThread(socket, logger, this);
                synchronized (activeResponseHandlers) {
                    activeResponseHandlers.add(responseHandlerThread);
                }
                responseHandlerThread.start();
            }
            // Else, failed connection. Already logged. Move on...
        }

    }

    @Override
    public void shutDown() {
        isGracefulShutDownRequested = true;
        logger.log(Level.INFO, "Server shut down requested. Interrupt again to attempt force shut down.");
    }

    @Override
    public void forceShutDown() {
        isGracefulShutDownRequested = true;
        logger.log(Level.INFO, "Force server shut down requested. Requesting all response handlers to terminate.");

        synchronized (activeResponseHandlers) {
            for (ResponseHandlerThread thread : activeResponseHandlers) {
                thread.interrupt();
            }
        }
    }

    @Override
    public void onThreadActionFinished(Thread finishedThread) {
        synchronized (activeResponseHandlers) {
            //noinspection SuspiciousMethodCalls
            activeResponseHandlers.remove(finishedThread);
        }
    }

    private ServerSocket getServerSocket() {
        ServerSocket serverSocket;
        logger.log(Level.FINE, String.format("Opening server socket at port %d...", port));
        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.FINE, "Server socket opened at port " + port.toString());
        } catch (IOException e) {
            serverSocket = null;
            logger.log(Level.SEVERE, "Failed to create server socket with message: " + e.getMessage());
        }
        return serverSocket;
    }

    private Socket accept(ServerSocket serverSocket) {
        Socket socket;
        logger.log(Level.FINE, "Listening....");
        try {
            socket = serverSocket.accept();
            logger.log(Level.FINE, "New request accepted over server socket");
        } catch (IOException e) {
            socket = null;
            logger.log(Level.SEVERE, "Failed to accept request over server socket with message: " + e.getMessage());
        }

        return socket;
    }
}
