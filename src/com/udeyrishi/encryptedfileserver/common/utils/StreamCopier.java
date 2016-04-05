package com.udeyrishi.encryptedfileserver.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rishi on 2016-04-02.
 */
public class StreamCopier implements Runnable {
    private static final Logger logger = LoggerFactory.createConsoleLogger(StreamCopier.class.getName());
    private static final int BUFFER_SIZE = Config.getConfig().getInt("BUFFER_SIZE_BYTES");
    private static final int RETRY_DELAY_MS = Config.getConfig().getInt("STREAM_COPIER_RETRY_DELAY_MS");
    private static final int RETRY_COUNT = Config.getConfig().getInt("STREAM_COPIER_RETRY_COUNT");

    private final InputStream in;
    private final OutputStream out;
    private final boolean terminateIfThreadInterrupted;
    private final long size;
    private final boolean sizeUnknown;

    public StreamCopier(OutputStream out, InputStream in) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = false;
        this.size = BUFFER_SIZE;
        this.sizeUnknown = true;
    }

    public StreamCopier(OutputStream out, InputStream in, boolean terminateIfThreadInterrupted) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = terminateIfThreadInterrupted;
        this.size = BUFFER_SIZE;
        this.sizeUnknown = true;
    }

    public StreamCopier(OutputStream out, InputStream in, long size) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = false;
        this.size = size;
        this.sizeUnknown = false;
    }

    public StreamCopier(OutputStream out, InputStream in, long size, boolean terminateIfThreadInterrupted) {
        this.out = Preconditions.checkNotNull(out, "out");
        this.in = Preconditions.checkNotNull(in, "in");
        this.terminateIfThreadInterrupted = terminateIfThreadInterrupted;
        this.size = size;
        this.sizeUnknown = false;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[(int) Math.min(BUFFER_SIZE, size)];

        try {
            int done = 0;
            int count;
            while ((sizeUnknown || done < size) && !isInterrupted()) {

                count = in.read(buffer);
                for (int i = 0; count < 0 && !sizeUnknown && i < RETRY_COUNT; ++i) {
                    // Client is too fast maybe. Slow down and retry.
                    logger.log(Level.FINE, String.format("The stream could be lagging behind. Slowing down and retrying " +
                            "after %d ms", RETRY_DELAY_MS));
                    Thread.sleep(RETRY_DELAY_MS);
                    count = in.read(buffer);
                }

                if (count < 0) {
                    if (sizeUnknown) {
                        break;
                    } else {
                        out.flush();
                        throw new StreamCorruptedException(String.format("Expected %d bytes, but only got %d", size, done));
                    }
                }
                out.write(buffer, 0, count);
                done += count;
                if (!sizeUnknown) {
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
            out.flush();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInterrupted() {
        return terminateIfThreadInterrupted && Thread.currentThread().isInterrupted();
    }
}
