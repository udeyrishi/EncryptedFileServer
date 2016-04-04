package com.udeyrishi.encryptedfileserver.common.communication.message.filters;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-03.
 */
public class PaddingFilter implements IncomingMessageFilter, OutgoingMessageFilter {

    private final byte messageSizeMultipleOf;

    public PaddingFilter(byte messageSizeMultipleOf) {
        this.messageSizeMultipleOf = messageSizeMultipleOf;
        if (messageSizeMultipleOf <= 0) {
            throw new IllegalArgumentException("Message size needs to between 1 and " + Byte.MAX_VALUE);
        }
    }

    @Override
    public InputStream filterIncomingMessage(final InputStream messageStream) {
        return new InputStream() {
            private byte[] buffer1 = new byte[messageSizeMultipleOf];
            private byte[] buffer2 = new byte[messageSizeMultipleOf];

            private int count = 0;
            private boolean start = true;
            private int buffer2Count;

            @Override
            public int read() throws IOException {
                if (start) {
                    start = false;
                    if (readUntilBufferFull(buffer2) != messageSizeMultipleOf) {
                        throw new RuntimeException("First packet must be full.");
                    }
                }

                count %= messageSizeMultipleOf;
                if (count == 0) {
                    buffer1 = buffer2;
                    if (messageStream.available() == 0) {
                        buffer2Count = 0;
                        buffer2 = null;
                    } else {
                        buffer2 = new byte[messageSizeMultipleOf];
                        buffer2Count = readUntilBufferFull(buffer2);
                    }
                }

                if (buffer2Count > 0) {
                    // Next buffer still has data. Can't be terminating in buffer 1
                    return buffer1[count++];
                } else if (buffer1 == null) {
                    return -1;
                } else {
                    // This is the last buffer
                    byte padCount = buffer1[buffer1.length - 1];
                    if (count < buffer1.length - padCount) {
                        return buffer1[count++];
                    } else {
                        return -1;
                    }
                }

            }

            private int readUntilBufferFull(byte[] buffer) throws IOException {
                int offset = 0;
                int length = buffer.length;

                while (length != 0) {
                    int count = messageStream.read(buffer, offset, length);
                    if (count < 0) {
                        break;
                    }
                    offset += count;
                    length -= count;
                }

                return offset;
            }
        };
    }

    @Override
    public InputStream filterOutgoingMessage(final InputStream message) {
        return new InputStream() {
            private byte count = 0;
            private int numPadCountValuesSent = 0;
            private byte padCountVal = 0;

            @Override
            public int read() throws IOException {
                int val = message.read();
                if (val >= 0) {
                    count = (byte)((count + 1) % messageSizeMultipleOf);
                    return val;
                } else {
                    if (numPadCountValuesSent == 0) {
                        padCountVal = (byte) (messageSizeMultipleOf - count);
                    }

                    if (numPadCountValuesSent < padCountVal) {
                        ++numPadCountValuesSent;
                        return padCountVal;
                    } else {
                        return val;
                    }
                }
            }
        };
    }
}
