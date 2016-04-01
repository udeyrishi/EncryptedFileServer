package com.udeyrishi.encryptedfileserver.common.tea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by rishi on 2016-03-31.
 */
public class TEAKeyReader {

    public HashMap<String, TEAKey> getAuthenticationKeys(String pathToKeys)
            throws IOException, BadTEAKeysFileException {

        HashMap<String, TEAKey> userIDsAndKeys = new HashMap<>();
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
}
