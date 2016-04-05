package com.udeyrishi.encryptedfileserver.common.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by rishi on 2016-03-30.
 */
public class LoggerFactory {
    private static Level DEFAULT_LOG_LEVEL = Level.ALL;

    public static Logger createConsoleLogger(String name) {
        Level logLevel = getLogLevel();
        Logger logger = Logger.getLogger(name);
        logger.setLevel(logLevel);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(logLevel);
        logger.addHandler(consoleHandler);
        return logger;
    }

    private static Level getLogLevel() {
        String settingsLogLevel = Config.getConfig().getString("LOG_LEVEL");
        Level logLevel = DEFAULT_LOG_LEVEL;
        try {
            if (settingsLogLevel != null) {
                logLevel = Level.parse(settingsLogLevel);
            }
        } catch (IllegalArgumentException e) {
            // leave default
        }

        return logLevel;
    }
}
