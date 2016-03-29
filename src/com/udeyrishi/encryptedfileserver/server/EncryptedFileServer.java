package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;
import com.udeyrishi.encryptedfileserver.common.TEAKey;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class EncryptedFileServer implements MultiThreadedServer {
    private static final int MAX_SHUTDOWN_WAIT_SEC = 60;

    private final Integer port;
    private final Map<String, TEAKey> keys;
    private final Logger logger;
    private final ExecutorService executorService;

    private ServerSocket serverSocket;
    private boolean isServerShutdownRequested = false;

    EncryptedFileServer(Integer port, Map<String, TEAKey> keys, Logger logger, ExecutorService executorService) {
        this.port = Preconditions.checkNotNull(port, "port");
        this.keys = Preconditions.checkNotNull(keys, "keys");
        this.logger = Preconditions.checkNotNull(logger, "logger");
        this.executorService = Preconditions.checkNotNull(executorService, "executorService");
    }

    @Override
    public void run() {
        serverSocket = createServerSocket();
        if (serverSocket == null) {
            return;
        }

        while (!isServerShutdownRequested) {
            Socket socket = accept(serverSocket);
            if (socket != null) {
                executorService.submit(new ResponseHandler(socket, logger));
            }
            // Else, failed connection. Already logged. Move on...
        }

        try {
            if (executorService.awaitTermination(MAX_SHUTDOWN_WAIT_SEC, TimeUnit.SECONDS)) {
                logger.log(Level.FINE, "All response handlers terminated");
            } else {
                logger.log(Level.FINE, "Timed out while waiting for response handlers to terminate");
            }

        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Executor service interrupted while waiting for response handlers to terminate");
        }
    }

    @Override
    public void shutDown() {
        logger.log(Level.INFO, "Server shut down requested.");
        shutDownGracefully();
    }

    @Override
    public void forceShutDown() {
        logger.log(Level.INFO, "Force server shut down requested");
        shutDownGracefully();

        logger.log(Level.FINE, "Asking all response handlers to terminate immediately");
        executorService.shutdownNow();
    }

    private void shutDownGracefully() {
        if (!isServerShutdownRequested) {
            isServerShutdownRequested = true;
            shutDownServerSocket();

            logger.log(Level.FINE, "Asking all response handlers to terminate");
            executorService.shutdown();
        }
    }

    private void shutDownServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                logger.log(Level.FINE, "Successfully closed server socket");
            } else {
                logger.log(Level.FINE, "Server socket was never initialised. Nothing to close.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to close server socket");
        }
    }

    private ServerSocket createServerSocket() {
        ServerSocket serverSocket;
        logger.log(Level.FINE, String.format("Creating server socket at port %d...", port));
        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.FINE, "Server socket created at port " + port.toString());
        } catch (IOException e) {
            serverSocket = null;
            logger.log(Level.SEVERE, String.format("Failed to create server socket on port %d with message: %s",
                                                   port,
                                                   e.getMessage()));
        }
        return serverSocket;
    }

    private Socket accept(ServerSocket serverSocket) {
        Socket socket;
        logger.log(Level.FINE, "Listening....");
        try {
            socket = serverSocket.accept();
            logger.log(Level.FINE, "New request accepted over server socket");
        } catch (SocketException e) {
            socket = null;
            logger.log(Level.FINER, "SocketException caught in EncryptedFileServer.accept. Ignore this if a shutdown was " +
                                    "requested.");
        } catch (IOException e) {
            socket = null;
            logger.log(Level.SEVERE, "Failed to accept request over server socket with message: " + e.getMessage());
        }

        return socket;
    }
}
