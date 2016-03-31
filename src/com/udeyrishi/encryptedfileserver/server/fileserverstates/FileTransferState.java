package com.udeyrishi.encryptedfileserver.server.fileserverstates;

import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;

/**
 * Created by rishi on 2016-03-30.
 */
public class FileTransferState implements CommunicationProtocol.CommunicationProtocolState {
    public FileTransferState(String pathToFilesRootDir) {

    }

    @Override
    public void messageReceived(CommunicationProtocol protocol, Message message) throws IllegalStateException {

    }

    @Override
    public Message nextTransmissionMessage(CommunicationProtocol protocol) throws IllegalStateException {
        return null;
    }

    @Override
    public void interrupt(CommunicationProtocol protocol) {

    }
}
