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

package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by rishi on 2016-04-04.
 */
public class Config {
    private static final String COMMENT = "//";
    private static final String DELIMITER = "=";
    private static Config instance = null;
    private final String filename;
    private final HashMap<String, String> settings = new HashMap<>();

    private Config(String filename) {
        this.filename = Preconditions.checkNotNull(filename, "filename)");
    }

    public static void initialize(String filename) throws IOException {
        instance = new Config(filename);
        instance.read();
    }

    public static Config getConfig() {
        if (instance == null) {
            throw new IllegalStateException("Config has not yet been initialised.");
        }
        return instance;
    }

    public void read() throws IOException {
        if (filename != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (line.trim().isEmpty() || line.trim().startsWith(COMMENT)) {
                        continue;
                    }

                    String[] parts = line.split(DELIMITER);
                    String setting = parts[0].trim();
                    String value = parts[1].trim();
                    settings.put(setting, value);
                }
            }
        }
    }

    public <T> T get(String setting, ValueParser<T> valueParser) {
        if (settings.containsKey(setting)) {
            return valueParser.parse(settings.get(setting));
        } else {
            return null;
        }
    }

    public Integer getInt(String setting) {
        if (settings.containsKey(setting)) {
            return get(setting, ValueParsers.createIntegerParser(setting));
        } else {
            return null;
        }
    }

    public String getString(String setting) {
        if (settings.containsKey(setting)) {
            return get(setting, ValueParsers.createStringParser(setting));
        } else {
            return null;
        }
    }
}
