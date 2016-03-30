package com.udeyrishi.encryptedfileserver.common;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by rishi on 2016-03-30.
 */
public class LoggerFactory {
    private static Level LOG_LEVEL = Level.ALL;

    public static Logger createConsoleLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(LOG_LEVEL);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(LOG_LEVEL);
        logger.addHandler(consoleHandler);
        return logger;
    }
}
