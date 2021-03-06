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

package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.utils.StreamCopier;

import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-04-01.
 */
public class FileReceivalState implements CommunicationProtocolState {
    private static final Logger logger = LoggerFactory.createConsoleLogger(FileReceivalState.class.getName());

    private final PrintStream userOut;
    private final BufferedReader userIn;
    private Thread downloader = null;
    private String lastFileRequested = null;
    private String downloadPath = null;
    private boolean interrupted = false;

    public FileReceivalState(BufferedReader userIn, PrintStream userOut) {
        this.userIn = Preconditions.checkNotNull(userIn, "userIn");
        this.userOut = Preconditions.checkNotNull(userOut, "userOut");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException {
        if (lastFileRequested == null) {
            throw new IllegalStateException("Request file first via nextTransmissionMessage");
        }

        if (interrupted) {
            protocol.setState(CommunicationProtocol.TERMINATED_STATE);
            return;
        } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_FAILURE) &&
                message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.FILE_NOT_FOUND)) {
            userOut.println(String.format("Error: File '%s' not found on the server", lastFileRequested));
        } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_SUCCESS)) {
            if (message.getContent().equals(lastFileRequested)) {
                try {
                    downloadAttachment(message.getAttachmentStream(), message.getAttachmentSize());
                } catch (SecurityException e) {
                    userOut.println(String.format("Error: Destination '%s' is non-writable", downloadPath));
                    logger.log(Level.WARNING, e.toString(), e);
                    // Dump the stream
                    //noinspection ResultOfMethodCallIgnored
                    message.getAttachmentStream().read(new byte[(int)message.getAttachmentSize()]);
                }
            } else {
                logger.log(Level.SEVERE, "Incorrect file received: " + message.getContent());
                throw new BadMessageException("Incorrect file received: " + message.getContent());
            }
        } else {
            throw new BadMessageException("Unknown message type: " + message.getType());
        }

        lastFileRequested = null;
    }

    private void downloadAttachment(InputStream attachmentStream, long fileSize) throws IOException {
        File downloadFile = new File(downloadPath);
        //noinspection ResultOfMethodCallIgnored
        if (downloadFile.getParentFile() != null) {
            downloadFile.getParentFile().mkdirs();
            downloadFile.getParentFile().mkdirs();
        }

        try (FileOutputStream fileSaveStream = new FileOutputStream(downloadFile);) {
            downloader = new Thread(new StreamCopier(fileSaveStream, attachmentStream, fileSize, true));
            downloader.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    userOut.println("Download failed because of some error: " + e.toString());
                }
            });
            downloader.start();
            while (downloader.isAlive()) {
                userOut.print(".");
                Thread.sleep(5);
            }
            if (downloader.isInterrupted()) {
                userOut.println("\nDownload cancelled.");
            } else {
                userOut.println("\nFile downloading finished.");
            }
            downloader = null;
        } catch (InterruptedException e) {
            userOut.println("\nDownload cancelled.");
        }
    }

    @Override
    public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
        try {
            if (interrupted) {
                protocol.setState(CommunicationProtocol.TERMINATED_STATE);
                return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.TERMINATION_REQUEST, null);
            }

            userOut.print("Filename [ENTER to quit] >> ");
            lastFileRequested = userIn.readLine();

            if (lastFileRequested.trim().isEmpty() || interrupted) {
                protocol.setState(CommunicationProtocol.TERMINATED_STATE);
                return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.TERMINATION_REQUEST, null);
            }

            userOut.print("Download path [ENTER to quit] >> ");
            downloadPath = userIn.readLine();
            if (downloadPath.trim().isEmpty() || interrupted) {
                protocol.setState(CommunicationProtocol.TERMINATED_STATE);
                return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.TERMINATION_REQUEST, null);
            }

            return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.FILE_REQUEST, lastFileRequested);
        } catch (IOException e) {
            // Somehow failed to do I/O over userIn or userOut. No good way to handle this
            logger.log(Level.SEVERE, e.toString(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        this.interrupted = true;
        if (downloader != null) {
            downloader.interrupt();
        }
    }
}
