package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.ThreadFinishedCallback;

/**
 * Created by rishi on 2016-03-28.
 */
public interface MultiThreadedServer extends Runnable, ThreadFinishedCallback {
    void shutDown();
    void forceShutDown();
}
