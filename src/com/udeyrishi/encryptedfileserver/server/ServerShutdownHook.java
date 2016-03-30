package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class ServerShutdownHook extends Thread {
    private static final Logger logger = Logger.getLogger(ServerShutdownHook.class.getName());
    private final MultiThreadedServer server;
    private int count = 0;

    ServerShutdownHook(MultiThreadedServer server) {
        this.server = Preconditions.checkNotNull(server, "server");
    }

    @Override
    public void run() {
        switch (count) {
            case 0:
                ++count;
                logger.log(Level.INFO, "Shutdown interrupt received. Attempting graceful shut down. Interrupt again to " +
                                       "attempt force shut down");
                server.shutDown();
                break;
            case 1:
                ++count;
                logger.log(Level.INFO, "Shutdown interrupt received. Attempting force shut down");
                server.forceShutDown();
                break;
            default:
                break;
        }
    }
}
