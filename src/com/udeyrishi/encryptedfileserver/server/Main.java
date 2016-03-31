package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.ArgumentParser;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolFactory;
import com.udeyrishi.encryptedfileserver.server.fileserverstates.FileTransferState;
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
    private static final Logger logger = LoggerFactory.createConsoleLogger(Main.class.getName());
    public static final String PORT = "port";
    public static final String KEYS_FILE_PATH = "keys_file_path";
    public static final String FILE_SERVER_ROOT = "file_server_root";

    public static void main(String[] args) {
        final ArgumentParser arguments;
        try {
            arguments = getArguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            final Map<String, TEAKey> authenticationKeys
                    = Collections.unmodifiableMap(getAuthenticationKeys(arguments.<String>get(KEYS_FILE_PATH)));

            CommunicationProtocolFactory protocolFactory = new CommunicationProtocolFactory() {
                @Override
                public CommunicationProtocol createProtocolInstance() {
                    FileTransferState onAuthState = new FileTransferState(arguments.<String>get(FILE_SERVER_ROOT));

                    // Sharing keys object is fine, because it's thread-safe (read-only), and only first message requires this
                    return new CommunicationProtocol(new TEAAuthenticationState(authenticationKeys, onAuthState));
                }
            };

            MultiThreadedServer server = new CommunicationProtocolServer(arguments.<Integer>get(PORT),
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
                    userIDsAndKeys.put(userID, new TEAKey(TEAFileServerProtocolStandard.ENCRYPTION_KEY_BIT_COUNT,
                                                          key));
                } catch (IllegalArgumentException e) {
                    throw new BadTEAKeysFileException(pathToKeys, e);
                }
            }
        }

        return userIDsAndKeys;
    }

    private static ArgumentParser getArguments(String[] args) throws IllegalArgumentException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.addPositionalArg(PORT, 0, parser.createIntegerParser("port"));
        parser.addPositionalArg(KEYS_FILE_PATH, 1, parser.createStringParser("Path to the keys file"));
        parser.addPositionalArg(FILE_SERVER_ROOT, 2, parser.createStringParser("Path to the file server " +
                                                                                        "root"));
        parser.process();
        return parser;
    }
}
