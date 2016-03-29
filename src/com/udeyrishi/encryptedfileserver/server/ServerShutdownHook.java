package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.Preconditions;

/**
 * Created by rishi on 2016-03-28.
 */
public class ServerShutdownHook extends Thread {
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
                server.shutDown();
                break;
            case 1:
                ++count;
                server.forceShutDown();
                break;
            default:
                break;
        }
    }
}
