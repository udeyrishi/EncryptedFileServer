package com.udeyrishi.encryptedfileserver.client;

import com.udeyrishi.encryptedfileserver.client.clientstates.FileReceivalState;
import com.udeyrishi.encryptedfileserver.client.clientstates.TEAAuthenticationState;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.tea.BadTEAKeysFileException;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKeyReader;
import com.udeyrishi.encryptedfileserver.common.tea.TEAMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.ArgumentParser;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class Main {
    private static final String PORT = "port";
    private static final String KEY = "key";
    private static final String HOSTNAME = "host";
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final Logger logger = LoggerFactory.createConsoleLogger(Main.class.getName());

    public static void main(String[] args) {

        ArgumentParser arguments;
        try {
            arguments = getArgumentParser(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            Map<String, TEAKey> authKeys = new TEAKeyReader().getAuthenticationKeys(arguments.<String>get(KEY));

            if (authKeys.size() != 1) {
                throw new BadTEAKeysFileException(arguments.<String>get(KEY));
            }

            String userID = null;
            TEAKey teaKey = null;

            // Has only 1
            for (Map.Entry<String, TEAKey> authKey : authKeys.entrySet()) {
                userID = authKey.getKey();
                teaKey = authKey.getValue();
            }

            logger.log(Level.FINER, "Key read from file: " + arguments.<String>get(KEY));
            logger.log(Level.FINER, "Hostname: " + arguments.<String>get(HOSTNAME));
            logger.log(Level.FINER, "Server port: " + arguments.<Integer>get(PORT).toString());

            TEAMessageFilter encryptionFiler = new TEAMessageFilter(teaKey);
            final CommunicationProtocol protocol = new CommunicationProtocol(new TEAAuthenticationState(userID,
                    new FileReceivalState(new BufferedReader(new InputStreamReader(System.in)), System.out)),
                    encryptionFiler, encryptionFiler);
            CommunicationProtocolClient client = new CommunicationProtocolClient(
                    arguments.<String>get(HOSTNAME),
                    arguments.<Integer>get(PORT),
                    protocol);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    protocol.interrupt();
                }
            });
            client.run();

        } catch (IOException | BadTEAKeysFileException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }

    private static ArgumentParser getArgumentParser(String[] args) throws IllegalArgumentException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.addPositionalArg(PORT, parser.createIntegerParser("The server's port"));
        parser.addPositionalArg(KEY, parser.createStringParser("The path to the key file"));
        parser.addOptionalArg(HOSTNAME, parser.createStringParser("The server's complete hostname."), DEFAULT_HOSTNAME);
        parser.process();
        return parser;
    }
}
