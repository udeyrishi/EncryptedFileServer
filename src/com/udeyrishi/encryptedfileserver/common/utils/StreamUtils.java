package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class StreamUtils {
    public static void copyOverStreams(OutputStream out, InputStream in) throws IOException {
        int count;
        byte[] buffer = new byte[8192];
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        out.flush();
    }
}
