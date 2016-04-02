package com.udeyrishi.encryptedfileserver.client.clientstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.*;
import java.nio.file.Files;
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
    private boolean first = true;
    private String lastFileRequested = null;

    public FileReceivalState(BufferedReader userIn, PrintStream userOut) {
        this.userIn = Preconditions.checkNotNull(userIn, "userIn");
        this.userOut = Preconditions.checkNotNull(userOut, "userOut");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) throws IOException, BadMessageException {
        if (lastFileRequested == null) {
            throw new IllegalStateException("Request file first via nextTransmissionMessage");
        }

        if (message.isEqualTo(TEAFileServerProtocolStandard.StandardMessages.FILE_NOT_FOUND_RESPONSE)) {
            userOut.println(String.format("Error: File '%s' Not Found", lastFileRequested));
        } else if (message.getTypeName().equals(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_SUCCESS)) {
            if (message.getMessageContents().equals(lastFileRequested) && message.getAttachmentStream() != null) {
                userOut.print("Download path>> ");
                String downloadPath = userIn.readLine();
                FileOutputStream fileSaveStream = new FileOutputStream(downloadPath);
                InputStream attachmentStream = message.getAttachmentStream();
                downloadAttachment(attachmentStream, fileSaveStream);
                fileSaveStream.close();
                userOut.println("File downloaded at " + downloadPath);
            } else {
                throw new BadMessageException(message.serializeMessage());
            }
        } else {
            throw new BadMessageException(message.serializeMessage());
        }
        lastFileRequested = null;
    }

    private void downloadAttachment(InputStream attachmentStream, OutputStream fileSaveStream) throws IOException {
        int count;
        byte[] buffer = new byte[8192];
        while ((count = attachmentStream.read(buffer)) > 0) {
            fileSaveStream.write(buffer, 0, count);
        }
        fileSaveStream.flush();
    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        if (first) {
            first = false;
            userOut.println("Connection made. Press CTRL + C to terminate: ");
        }
        userOut.print("Filename>> ");
        try {
            lastFileRequested = userIn.readLine();
            return TEAFileServerProtocolStandard.StandardMessages.fileRequest(lastFileRequested);
        } catch (IOException e) {
            // TODO: Fix this
            logger.log(Level.SEVERE, e.toString(), e);
//            interrupt(protocol);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {

    }
}
