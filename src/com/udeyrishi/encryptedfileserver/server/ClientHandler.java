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

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.utils.StreamCopier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
class ClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.createConsoleLogger(ClientHandler.class.getName());
    private final Socket socket;
    private final CommunicationProtocol protocol;

    ClientHandler(Socket socket, CommunicationProtocol protocol) {
        this.socket = Preconditions.checkNotNull(socket, "socket");
        this.protocol = Preconditions.checkNotNull(protocol, "protocol");
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            while (true) {
                // TODO: The shouldTerminate() thingie doesn't work. It won't send back an interrupt message ever
                try {
                    IncomingRequestMessage incomingRequestMessage = new IncomingRequestMessage(in);
                    protocol.processReceivedMessage(incomingRequestMessage);
                    logger.log(Level.FINEST, "Rx message processing completed");
                } catch (BadMessageException e) {
                    logger.log(Level.WARNING, "Illegal message received from client. Ignoring and moving on.", e);
                }
                if (shouldTerminate()) {
                    break;
                }

                InputStream responseStream = protocol.getNextTransmissionMessage().getStream();
                new StreamCopier(out, responseStream).run();
                responseStream.close();

                logger.log(Level.FINEST, "Tx message sent");
                if (shouldTerminate()) {
                    break;
                }
            }
        } catch (IOException | IllegalStateException e) {
            logger.log(Level.WARNING, e.toString(), e);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unhandled exception in ClientHandler", e);
        } catch (UnsatisfiedLinkError e) {
            logger.log(Level.SEVERE, "TEA native library not in proper location.", e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to close socket after completing communications.", e);
        }

        logger.log(Level.FINER, "Shutting down client handler");
    }

    private boolean shouldTerminate() {
        if (Thread.currentThread().isInterrupted()) {
            protocol.interrupt();
        }

        return protocol.isTerminated();
    }
}
