package com.udeyrishi.encryptedfileserver.client;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingResponseMessage;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessage;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;
import com.udeyrishi.encryptedfileserver.common.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            while (true) {
                OutgoingMessage requestMessage = protocol.getNextTransmissionMessage();
                if (protocol.isTerminated()) {
                    break;
                }

                InputStream requestMessageStream = requestMessage.getStream();
                StreamUtils.copyOverStreams(out, requestMessageStream);
                requestMessageStream.close();

                IncomingResponseMessage received = new IncomingResponseMessage(in);
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
