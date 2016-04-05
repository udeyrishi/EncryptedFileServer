package com.udeyrishi.encryptedfileserver.common.utils;

import java.nio.ByteBuffer;

/**
 * Source: http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
 */

public class ByteUtils {

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static void copyBuffer(byte[] dest, byte[] source, int offsetDest, int offsetSource, int length) {
        for (int i = 0; i < length; ++i) {
            dest[offsetDest + i] = source[offsetSource + i];
        }
    }
}