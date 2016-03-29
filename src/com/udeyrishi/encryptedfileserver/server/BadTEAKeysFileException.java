package com.udeyrishi.encryptedfileserver.server;

/**
 * Created by rishi on 2016-03-28.
 */
public class BadTEAKeysFileException extends Exception {
    BadTEAKeysFileException(String fileName, Throwable cause) {
        super(fileName + " file is not in proper keys file format.", cause);
    }

    @Override
    public String getMessage() {
        if (getCause() == null) {
            return super.getMessage();
        } else {
            return super.getMessage() + "\nCause: " + super.getCause().getClass() + ": " + super.getCause().getMessage();
        }
    }
}
