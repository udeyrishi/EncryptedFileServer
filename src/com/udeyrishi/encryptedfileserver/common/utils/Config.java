package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by rishi on 2016-04-04.
 */
public class Config {
    private final String filename;
    private final HashMap<String, String> settings = new HashMap<>();
    private static Config instance = null;

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

    private Config(String filename) {
        this.filename = Preconditions.checkNotNull(filename, "filename)");
    }

    public void read() throws IOException {
        if (filename != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    String[] parts = line.split("=");
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
