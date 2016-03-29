package com.udeyrishi.encryptedfileserver.server;

/**
 * Created by rishi on 2016-03-28.
 */
public interface MultiThreadedServer extends Runnable {
    void shutDown();
    void forceShutDown();
}
