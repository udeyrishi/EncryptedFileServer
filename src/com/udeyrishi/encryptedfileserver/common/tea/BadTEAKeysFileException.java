package com.udeyrishi.encryptedfileserver.common.tea;

/**
 * Created by rishi on 2016-03-28.
 */
public class BadTEAKeysFileException extends Exception {
    public BadTEAKeysFileException(String fileName, Throwable cause) {
        super(fileName + " file is not in proper keys file format.", cause);
    }

    public BadTEAKeysFileException(String fileName) {
        super(fileName + " file is not in proper keys file format.");
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
