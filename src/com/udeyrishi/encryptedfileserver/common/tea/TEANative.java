package com.udeyrishi.encryptedfileserver.common.tea;

/**
 * Created by rishi on 2016-04-03.
 */
public class TEANative {
    static {
        System.loadLibrary("com_udeyrishi_encryptedfileserver_common_tea_TEANative");
    }

    public native void encrypt(byte[] data, long[] key);
    public native void decrypt(byte[] data, long[] key);
}
