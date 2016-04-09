/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package com.udeyrishi.encryptedfileserver.client;

import com.udeyrishi.encryptedfileserver.client.clientstates.FileReceivalState;
import com.udeyrishi.encryptedfileserver.client.clientstates.TEAAuthenticationState;
import com.udeyrishi.encryptedfileserver.common.communication.CommunicationProtocol;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.CompoundIncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.CompoundOutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.PaddingFilter;
import com.udeyrishi.encryptedfileserver.common.tea.BadTEAKeysFileException;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKey;
import com.udeyrishi.encryptedfileserver.common.tea.TEAKeyReader;
import com.udeyrishi.encryptedfileserver.common.tea.TEAMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.ArgumentParser;
import com.udeyrishi.encryptedfileserver.common.utils.Config;
import com.udeyrishi.encryptedfileserver.common.utils.LoggerFactory;
import com.udeyrishi.encryptedfileserver.common.utils.ValueParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AccessControlException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-03-28.
 */
public class Main {
    static {
        // Keep this at the top
        try {
            Config.initialize("client.config");
        } catch (Exception e) {
            throw new RuntimeException("Bad config file: client.config", e);
        }
    }

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
            PaddingFilter paddingFilter = new PaddingFilter((byte) (2 * Long.SIZE / Byte.SIZE));

            final CommunicationProtocol protocol = new CommunicationProtocol(new TEAAuthenticationState(userID,
                    new FileReceivalState(new BufferedReader(new InputStreamReader(System.in)), System.out)),
                    new CompoundIncomingMessageFilter(encryptionFiler, paddingFilter),
                    new CompoundOutgoingMessageFilter(paddingFilter, encryptionFiler));
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
        } catch (AccessControlException e) {
            logger.log(Level.FINE, e.toString(), e);
            System.err.println(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unhandled exception in client.", e);
        }
    }

    private static ArgumentParser getArgumentParser(String[] args) throws IllegalArgumentException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.addPositionalArg(PORT, ValueParsers.createIntegerParser("The server's port"));
        parser.addPositionalArg(KEY, ValueParsers.createStringParser("The path to the key file"));
        parser.addOptionalArg(HOSTNAME, ValueParsers.createStringParser("The server's complete hostname."), DEFAULT_HOSTNAME);
        parser.process();
        return parser;
    }
}
