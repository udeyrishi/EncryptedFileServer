/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class ServerShutdownHook extends Thread {
    private static final Logger logger = LoggerFactory.createConsoleLogger(ServerShutdownHook.class.getName());
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
