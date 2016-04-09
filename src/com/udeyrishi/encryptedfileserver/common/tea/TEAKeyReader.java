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
