package com.udeyrishi.encryptedfileserver.client;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.Message;
import com.udeyrishi.encryptedfileserver.common.communication.MessageBuilder;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-31.
 */
class CommunicationProtocolClient implements Runnable {
    private static final Logger logger = LoggerFactory.createConsoleLogger(CommunicationProtocolClient.class.getName());

    private final CommunicationProtocol protocol;
    private final String hostname;
    private final int port;

    CommunicationProtocolClient(String serverHostname, int serverPort, CommunicationProtocol protocol) {
        this.hostname = Preconditions.checkNotNull(serverHostname, "serverHostname");
        this.port = serverPort;
        this.protocol = Preconditions.checkNotNull(protocol, "protocol");
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream());
             InputStream attachmentIn = socket.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                Message requestMessage = protocol.getNextTransmissionMessage();
                if (protocol.isTerminated()) {
                    break;
                }
                out.println(requestMessage.serializeMessage());

                Message received = MessageBuilder.requestMessage().addTypeAndContent(in).addAttachmentStream(attachmentIn)
                        .autoCloseReader(false).build();
                protocol.processReceivedMessage(received);
                if (protocol.isTerminated()) {
                    break;
                }
            }

        } catch (IOException | BadMessageException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }

    }
}
