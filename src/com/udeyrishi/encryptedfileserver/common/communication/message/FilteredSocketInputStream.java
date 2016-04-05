package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.utils.ByteUtils;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by rishi on 2016-04-03.
 */
public class FilteredSocketInputStream extends InputStream {
    private final InputStream inputStream;
    private final FilterBufferAction action;
    private final int bufferActionBufferSize;

    // Need this because SocketInputStream is not public... Can't extend it
    public FilteredSocketInputStream(InputStream inputStream, FilterBufferAction action, int bufferActionBufferSize) {
        this.inputStream = Preconditions.checkNotNull(inputStream, "inputStream");
        this.action = Preconditions.checkNotNull(action, "action");
        this.bufferActionBufferSize = bufferActionBufferSize;
    }

    @Override
    public int read() throws IOException {
        byte[] temp = new byte[1];
        int n = this.read(temp, 0, 1);
        if (n <= 0) {
            return -1;
        }
        return temp[0] & 0xff;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        byte[] actionBuffer = new byte[bufferActionBufferSize];
        int count = inputStream.read(b, off, len);

        int processed = 0;
        while (processed < count) {
            int left = count - processed;
            int batchSize = Math.min(left, bufferActionBufferSize);
            ByteUtils.copyBuffer(actionBuffer, b, 0, off + processed, batchSize);
            ByteUtils.copyBuffer(actionBuffer, new byte[bufferActionBufferSize - batchSize], batchSize, 0,
                                 bufferActionBufferSize - batchSize);

            action.bufferAction(actionBuffer);
            ByteUtils.copyBuffer(b, actionBuffer, off + processed, 0, batchSize);
            processed += batchSize;
        }

        return count;
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
        void bufferAction(byte[] buffer) throws IOException;
    }

}
