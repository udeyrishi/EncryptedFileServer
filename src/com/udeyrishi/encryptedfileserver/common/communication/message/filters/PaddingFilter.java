package com.udeyrishi.encryptedfileserver.common.communication.message.filters;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-03.
 */
public class PaddingFilter implements IncomingMessageFilter, OutgoingMessageFilter {

    private final byte messageSizeMultipleOf;
    private ByteArrayOutputStream incomingCacheBuffer = null;

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
                    try {
                        readUntilBufferFull(buffer2);
                    } catch (BadMessageException e) {
                        throw new RuntimeException(e);
                    }
                    start = false;
                }

                count %= messageSizeMultipleOf;
                if (count == 0) {
                    buffer1 = buffer2;
                    if (messageStream.available() == 0) {
                        buffer2Count = 0;
                        buffer2 = null;
                    } else {
                        buffer2 = new byte[messageSizeMultipleOf];
                        try {
                            buffer2Count = readUntilBufferFull(buffer2);
                        } catch (BadMessageException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (buffer2Count > 0) {
                    // Next buffer still has data. Can't be terminating in buffer 1
                    return getAndCacheValueFromBuffer(buffer1, count++);
                } else if (buffer1 == null) {
                    return -1;
                } else {
                    // This is the last buffer
                    byte padCount = buffer1[buffer1.length - 1];
                    if (count < buffer1.length - padCount) {
                        return getAndCacheValueFromBuffer(buffer1, count++);
                    } else {
                        return -1;
                    }
                }

            }

            private int readUntilBufferFull(byte[] buffer) throws IOException, BadMessageException {
                int offset = 0;
                int length = buffer.length;

                while (length != 0) {
                    if (offset > 0 && messageStream.available() == 0) {
                        throw new BadMessageException("Expected message length to be " + buffer.length + " but got " + offset);
                    }
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

    private int getAndCacheValueFromBuffer(byte[] buffer, int index) {
        int returnVal = buffer[index];
        if (incomingCacheBuffer != null) {
            incomingCacheBuffer.write(returnVal & 0xFF);
        }
        return returnVal & 0xFF;
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
                if (val != -1) {
                    count = (byte) ((count + 1) % messageSizeMultipleOf);
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

    @Override
    public void turnOnRawMessageCaching() {
        incomingCacheBuffer = new ByteArrayOutputStream();
    }

    @Override
    public void turnOffRawMessageCaching() {
        incomingCacheBuffer = null;
    }

    @Override
    public InputStream getRawMessageCache() {
        if (incomingCacheBuffer == null) {
            return null;
        } else {
            return new ByteArrayInputStream(incomingCacheBuffer.toByteArray());
        }
    }

    @Override
    public boolean isRawMessageCachingSupported() {
        return true;
    }
}
