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
    private final long maxSize;

    public StreamCopier(OutputStream out, InputStream in, long maxSize) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = false;
        this.maxSize = maxSize;
    }

    public StreamCopier(OutputStream out, InputStream in, long maxSize, boolean terminateIfThreadInterrupted) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = terminateIfThreadInterrupted;
        this.maxSize = maxSize;
    }

    @Override
    public void run() {
        int count;
        byte[] buffer = new byte[(int)Math.min(BUFFER_SIZE, maxSize)];

        try {
            int done = 0;
            while (done < maxSize && !isInterrupted() && (count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                done += count;
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
