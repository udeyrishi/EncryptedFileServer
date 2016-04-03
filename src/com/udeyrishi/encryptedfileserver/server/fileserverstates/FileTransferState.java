package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolState;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingResponseMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by rishi on 2016-03-30.
 */
public class FileTransferState implements CommunicationProtocolState {
    private final String root;
    private String lastFileRequested = null;
    private boolean interrupted = false;

    public FileTransferState(String pathToFilesRootDir) {
        this.root = Preconditions.checkNotNull(pathToFilesRootDir, "pathToFilesRootDir");
    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, IncomingMessage message) throws IOException, BadMessageException {
        if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.FILE_REQUEST)) {
            lastFileRequested = message.getContent();
        } else if (message.getType().equals(TEAFileServerProtocolStandard.TypeNames.TERMINATION_REQUEST)) {
            protocol.setState(CommunicationProtocol.TERMINATED_STATE);
        }
    }

    @Override
    public OutgoingMessage nextTransmissionMessage(CommunicationProtocol protocol) {
        if (interrupted) {
            protocol.setState(CommunicationProtocol.TERMINATED_STATE);
            return new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.INTERRUPT_NOTIFICATION, null);
        }

        if (lastFileRequested == null) {
            return new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_FAILURE,
                    TEAFileServerProtocolStandard.SpecialContent.BAD_FILE_REQUEST);
        }

        OutgoingResponseMessage response;
        try {
            InputStream attachmentStream = Files.newInputStream(Paths.get(root, lastFileRequested));
            response = new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_SUCCESS,
                    lastFileRequested,
                    attachmentStream);
        } catch (IOException e) {
            response = new OutgoingResponseMessage(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_FAILURE,
                    TEAFileServerProtocolStandard.SpecialContent.FILE_NOT_FOUND);
        }
        lastFileRequested = null;
        return response;
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        interrupted = true;
    }
}
