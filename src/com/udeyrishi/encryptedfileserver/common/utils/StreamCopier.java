package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class StreamCopier implements Runnable {
    private static final int BUFFER_SIZE = 8192;
    private final InputStream in;
    private final OutputStream out;
    private final boolean terminateIfThreadInterrupted;

    public StreamCopier(OutputStream out, InputStream in) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = false;
    }

    public StreamCopier(OutputStream out, InputStream in, boolean terminateIfThreadInterrupted) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = terminateIfThreadInterrupted;
    }

    @Override
    public void run() {
        Preconditions.checkNotNull(out, "out");
        Preconditions.checkNotNull(in, "in");

        int count;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            while (!isInterrupted() && (count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                if (count < BUFFER_SIZE) {
                    // Some InputStreams could be blocking
                    break;
                }
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInterrupted() {
        return terminateIfThreadInterrupted && Thread.currentThread().isInterrupted();
    }
}
