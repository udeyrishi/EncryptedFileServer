package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-03.
 */
public class FilteredSocketInputStream extends InputStream {
    private final InputStream inputStream;
    private final FilterBufferAction action;
    private final byte[] buffer;

    // Need this because SocketInputStream is not public... Can't extend it
    public FilteredSocketInputStream(InputStream inputStream, FilterBufferAction action, int bufferSize) {
        this.inputStream = Preconditions.checkNotNull(inputStream, "inputStream");
        this.action = Preconditions.checkNotNull(action, "action");
        this.buffer = new byte[bufferSize];
    }

    private int i = 0;
    private int readCount = Integer.MAX_VALUE;

    @Override
    public int read() throws IOException {
        i %= readCount;

        if (i == 0) {
            readCount = inputStream.read(buffer);
            if (readCount > 0) {
                action.bufferAction(buffer);
                return buffer[i++];
            } else {
                return -1;
            }
        } else {
            return buffer[i++];
        }
    }

    @Override
    public int read(byte b[]) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readLimit) {
        inputStream.mark(readLimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public interface FilterBufferAction {
        void bufferAction(byte[] buffer);
    }

}
