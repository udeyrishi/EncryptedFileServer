package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolFactory;
import com.udeyrishi.encryptedfileserver.server.fileserverstates.TEAAuthenticationState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final int ENCRYPTION_KEY_BIT_COUNT = 256;
    private static final Logger logger = LoggerFactory.createConsoleLogger(Main.class.getName());

    public static void main(String[] args) {
        ServerArguments arguments;
        try {
            arguments = new ServerArguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            final Map<String, TEAKey> authenticationKeys
                    = Collections.unmodifiableMap(getAuthenticationKeys(arguments.getPathToKeys()));

            CommunicationProtocolFactory protocolFactory = new CommunicationProtocolFactory() {
                @Override
                public CommunicationProtocol createProtocolInstance() {
                    // Sharing keys object is fine, because it's thread-safe (read-only), and only first message requires this
                    return new CommunicationProtocol(new TEAAuthenticationState(authenticationKeys));
                }
            };

            MultiThreadedServer server = new CommunicationProtocolServer(arguments.getPort(),
                                                                 protocolFactory,
                                                                 Executors.newCachedThreadPool());
            Runtime.getRuntime().addShutdownHook(new ServerShutdownHook(server));
            server.run();

        } catch (IllegalArgumentException | IOException | BadTEAKeysFileException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }

        logger.log(Level.INFO, "Shut down completed");
    }

    private static ConcurrentHashMap<String, TEAKey> getAuthenticationKeys(String pathToKeys)
                                                                    throws IOException, BadTEAKeysFileException {

        ConcurrentHashMap<String, TEAKey> userIDsAndKeys = new ConcurrentHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathToKeys))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] parts = line.split("\\s+", 2);

                String userID = parts[0];
                String key = parts[1];
                try {
                    userIDsAndKeys.put(userID, new TEAKey(ENCRYPTION_KEY_BIT_COUNT, key));
                } catch (IllegalArgumentException e) {
                    throw new BadTEAKeysFileException(pathToKeys, e);
                }
            }
        }

        return userIDsAndKeys;
    }

    private static class ServerArguments {
        private Integer port;
        private String pathToKeys;

        ServerArguments(String[] args) {
            if (args.length < 2) {
                throw new IllegalArgumentException(getUsage());
            }

            try {
                this.port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(getUsage());
            }
            this.pathToKeys = args[1];
        }

        private static String getUsage() {
            return "Usage: java Main <port_number> <keys_file_path>";
        }

        String getPathToKeys() {
            return pathToKeys;
        }

        Integer getPort() {
            return port;
        }
    }
}
