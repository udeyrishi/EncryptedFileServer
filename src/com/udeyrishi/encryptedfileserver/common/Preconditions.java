package com.udeyrishi.encryptedfileserver.common;

/**
 * Created by rishi on 2016-03-28.
 */
public class Preconditions {
    public static <T> T checkNotNull(T obj, String argName) {
        if (obj == null) {
            throw new NullPointerException(String.format("Argument: '%s' can't be null.", argName));
        }

        return obj;
    }
}
