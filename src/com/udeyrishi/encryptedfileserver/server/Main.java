package com.udeyrishi.encryptedfileserver.server;

import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocolFactory;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.PaddingFilter;
import com.udeyrishi.encryptedfileserver.common.tea.BadTEAKeysFileException;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKeyReader;
import com.udeyrishi.encryptedfileserver.common.utils.ArgumentParser;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.server.fileserverstates.FileTransferState;
import com.udeyrishi.encryptedfileserver.server.fileserverstates.TEAAuthenticationState;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = LoggerFactory.createConsoleLogger(Main.class.getName());
    private static final String PORT = "port";
    private static final String KEYS_FILE_PATH = "keys";
    private static final String FILE_SERVER_ROOT = "root";
    private static final String DEFAULT_ROOT = ".";

    public static void main(String[] args) {
        final ArgumentParser arguments;
        try {
            arguments = getArguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            final Map<String, TEAKey> authenticationKeys = Collections.unmodifiableMap(
                    new ConcurrentHashMap<>(
                            new TEAKeyReader().getAuthenticationKeys(arguments.<String>get(KEYS_FILE_PATH))));

            logger.log(Level.FINER, "Keys read from file: " + arguments.<String>get(KEYS_FILE_PATH));
            logger.log(Level.FINER, "File server root: " + arguments.<String>get(FILE_SERVER_ROOT));


            CommunicationProtocolFactory protocolFactory = new CommunicationProtocolFactory() {
                @Override
                public CommunicationProtocol createProtocolInstance() {
                    FileTransferState onAuthState = new FileTransferState(arguments.<String>get(FILE_SERVER_ROOT));
                    final PaddingFilter paddingFilter = new PaddingFilter((byte) (2 * Long.SIZE / Byte.SIZE));

                    // Sharing keys object is fine, because it's thread-safe (read-only), and only first message requires this
                    return new CommunicationProtocol(new TEAAuthenticationState(authenticationKeys, onAuthState),
                            paddingFilter, paddingFilter);
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

    private static ArgumentParser getArguments(String[] args) throws IllegalArgumentException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.addPositionalArg(PORT, parser.createIntegerParser("The server's port"));
        parser.addPositionalArg(KEYS_FILE_PATH, parser.createStringParser("Path to the keys file"));
        parser.addOptionalArg(FILE_SERVER_ROOT, parser.createStringParser("Path to the file server root"),
                DEFAULT_ROOT);
        parser.process();
        return parser;
    }
}
