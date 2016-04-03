package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingResponseMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingRequestMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-04-01.
 */
public class FileReceivalState implements CommunicationProtocolState {
    private static final Logger logger = LoggerFactory.createConsoleLogger(FileReceivalState.class.getName());

    private final PrintStream userOut;
    private final BufferedReader userIn;
    private boolean first = true;
    private String lastFileRequested = null;
    private String downloadPath = null;

    public FileReceivalState(BufferedReader userIn, PrintStream userOut) {
        this.userIn = Preconditions.checkNotNull(userIn, "userIn");
        this.userOut = Preconditions.checkNotNull(userOut, "userOut");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException {
        if (lastFileRequested == null) {
            throw new IllegalStateException("Request file first via nextTransmissionMessage");
        }

        if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_FAILURE) &&
                message.getContent().equals(TEAFileServerProtocolStandard.SpecialContent.FILE_NOT_FOUND)) {
            userOut.println(String.format("Error: File '%s' not found on the server", lastFileRequested));

        } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_SUCCESS)) {
            if (message.getContent().equals(lastFileRequested)) {
                downloadAttachment(((IncomingResponseMessage) message).getAttachmentStream());
            } else {
                logger.log(Level.SEVERE, "Incorrect file received: " + message.getContent());
                throw new BadMessageException("Incorrect file received: " + message.getContent());
            }

        } else {
            throw new BadMessageException("Unknown message type: " + message.getType());
        }

        lastFileRequested = null;
    }

    private void downloadAttachment(InputStream attachmentStream) throws IOException {
        try (FileOutputStream fileSaveStream = new FileOutputStream(downloadPath)) {
            int count;
            byte[] buffer = new byte[8192];
            while ((count = attachmentStream.read(buffer)) > 0) {
                fileSaveStream.write(buffer, 0, count);
                if (count < 8192) {
                    break;
                }
            }
            fileSaveStream.flush();
            userOut.println("File downloaded at " + downloadPath);
        } catch (FileNotFoundException e) {
            userOut.println("Download path doesn't exist: " + downloadPath);
        }
    }

    @Override
    public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
        if (first) {
            first = false;
            userOut.println("Connection made. Press CTRL + C to terminate: ");
        }

        try {
            lastFileRequested = getNonNullInput("Filename>> ", "Filename can't be empty");
            downloadPath = getNonNullInput("Download path>>", "Download path can't be empty");

            return new OutgoingRequestMessage(TEAFileServerProtocolStandard.TypeNames.FILE_REQUEST, lastFileRequested);
        } catch (IOException e) {
            // Somehow failed to do I/O over userIn or userOut. No good way to handle this
            logger.log(Level.SEVERE, e.toString(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {

    }

    private String getNonNullInput(String prompt, String errorMessage) throws IOException {
        String input;

        while (true) {
            userOut.print(prompt);
            input = userIn.readLine().trim();
            if (input.isEmpty()) {
                userOut.println(errorMessage);
            } else {
                break;
            }
        }

        return input;
    }
}
