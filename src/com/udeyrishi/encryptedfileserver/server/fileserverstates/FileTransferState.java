package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.communication.*;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    public void messageReceived(CommunicationProtocol protocol, Message message) throws IOException, BadMessageException {
        if (message.getTypeName().equals(TEAFileServerProtocolStandard.TypeNames.FILE_REQUEST)) {
            lastFileRequested = message.getMessageContents();
        } else if (TEAFileServerProtocolStandard.StandardMessages.TERMINATION_REQUEST.isEqualTo(message)) {
            protocol.setState(CommunicationProtocol.TERMINATED_STATE);
        }
    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) {
        if (interrupted) {
            protocol.setState(CommunicationProtocol.TERMINATED_STATE);
            return MessageBuilder.responseMessage().addType(TEAFileServerProtocolStandard.TypeNames.INTERRUPT_NOTIFICATION)
                                                    .build();
        }

        if (lastFileRequested == null) {
            return TEAFileServerProtocolStandard.StandardMessages.BAD_FILE_REQUEST_RESPONSE;
        }

        Message response;
        try {
            response = MessageBuilder.responseMessage()
                    .addType(TEAFileServerProtocolStandard.TypeNames.FILE_RESPONSE_SUCCESS)
                    .addContent(new BufferedReader(new FileReader(Paths.get(root, lastFileRequested).toFile())))
                    .autoCloseStream(true)
                    .build();

        } catch (FileNotFoundException e) {
            response = TEAFileServerProtocolStandard.StandardMessages.FILE_NOT_FOUND_RESPONSE;
        }
        lastFileRequested = null;
        return response;
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {
        interrupted = true;
    }
}
