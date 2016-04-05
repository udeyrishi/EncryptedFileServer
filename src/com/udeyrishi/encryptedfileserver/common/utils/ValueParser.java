package com.udeyrishi.encryptedfileserver.common.utils;

/**
 * Created by rishi on 2016-04-04.
 */
public interface ValueParser<T> {
    String getDescription();

    String getParsedTypeName();

    T parse(String argValue) throws IllegalArgumentException;
}
