package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by rishi on 2016-03-30.
 */
public class LoggerFactory {
    private static final Level DEBUG_LEVEL = Level.ALL;
    private static final Level PROD_LEVEL = Level.INFO;
    private static Level DEFAULT_LOG_LEVEL = DEBUG_LEVEL;

    public static Logger createConsoleLogger(String name) {
        Level logLevel = getLogLevel();
        Logger logger = Logger.getLogger(name);
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        Handler handler;
        boolean failed = false;
        String logTarget = Config.getConfig().getString("LOG_TARGET");
        if (logTarget == null || logTarget.toLowerCase().equals("console")) {
            handler = new ConsoleHandler();
        } else {
            try {
                handler = new FileHandler(name + "." + logTarget);
            } catch (IOException e) {
                failed = true;
                handler = new ConsoleHandler();
            }
        }
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(logLevel);
        logger.addHandler(handler);

        if (failed) {
            logger.log(Level.WARNING,
                    String.format("Failed to create logger for file '%s'. Making console logger instead", logTarget));
        }
        return logger;
    }

    private static Level getLogLevel() {
        String settingsLogLevel = Config.getConfig().getString("LOG_LEVEL");
        String appMode = Config.getConfig().getString("MODE");

        if (settingsLogLevel != null) {
            try {
                return Level.parse(settingsLogLevel);
            } catch (IllegalArgumentException e) {
                if (appMode != null) {
                    return getAppModeBasedLogLevel(appMode);
                } else {
                    return DEFAULT_LOG_LEVEL;
                }
            }
        } else if (appMode != null) {
            return getAppModeBasedLogLevel(appMode);
        } else {
            return DEFAULT_LOG_LEVEL;
        }
    }

    private static Level getAppModeBasedLogLevel(String appMode) {
        if (appMode.toLowerCase().equals("prod")) {
            return PROD_LEVEL;
        } else {
            return DEBUG_LEVEL;
        }
    }
}
