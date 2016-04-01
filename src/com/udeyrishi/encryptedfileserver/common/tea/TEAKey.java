package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.utils.BigIntegerParser;

import java.math.BigInteger;

/**
 * Created by rishi on 2016-03-28.
 */
public class TEAKey {

    private final int bitCount;
    private final BigInteger key;

    public TEAKey(int bitCount, String key) throws IllegalArgumentException {
        if (bitCount < 0 || bitCount % 8 != 0) {
            throw new IllegalArgumentException("bitCount needs to be a positive multiple of 8.");
        }
        this.bitCount = bitCount;
        this.key = BigIntegerParser.parseBigInteger(key);

        if (!isKeySizeValid(this.key, bitCount)) {
            throw new IllegalArgumentException(String.format("Key needs to fit in a %d-bit unsigned number.", bitCount));
        }

    }

    private static boolean isKeySizeValid(BigInteger key, int bitCount) {
        // "ff" (bitCount/8) times == (bitCount/8) bytes == bitCount bits
        final BigInteger maxKeyVal = new BigInteger(new String(new char[bitCount / 8]).replace("\0", "ff"), 16);
        return key.compareTo(maxKeyVal) <= 0 && key.compareTo(BigInteger.ZERO) >= 0;
    }

    public long getPart(int partNumber) throws IllegalArgumentException {
        final int upperBound = numLongs() - 1;
        if (partNumber > upperBound || partNumber < 0) {
            throw new IllegalArgumentException(String.format("partNumber needs to be between 0 and %d", upperBound));
        }
        return key.shiftRight(64 * (upperBound - partNumber)).longValue();
    }

    public long[] getAsLongArray() {
        long[] keyItems = new long[numLongs()];

        for (int i = 0; i < numLongs(); ++i) {
            keyItems[i] = getPart(i);
        }

        return keyItems;
    }

    @Override
    public String toString() {
        return "0x" + key.toString(16);
    }

    private int numLongs() {
        return bitCount / 64;
    }

}
