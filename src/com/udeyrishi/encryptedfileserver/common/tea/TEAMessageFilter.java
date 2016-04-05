package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.message.FilteredSocketInputStream;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.OutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.ByteUtils;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAMessageFilter implements IncomingMessageFilter, OutgoingMessageFilter {

    private final TEAKey key;
    private final TEANative nativeLib;
    private ByteArrayOutputStream incomingCacheBuffer = null;

    public TEAMessageFilter(TEAKey key) {
        this.key = Preconditions.checkNotNull(key, "key");
        this.nativeLib = new TEANative();
    }

    @Override
    public InputStream filterIncomingMessage(final InputStream inputStream) {
        return new FilteredSocketInputStream(inputStream, new FilteredSocketInputStream.FilterBufferAction() {
            @Override
            public void bufferAction(byte[] buffer) throws IOException {
                if (incomingCacheBuffer != null) {
                    for (byte b : buffer) {
                        incomingCacheBuffer.write(b);
                    }
                }
                nativeLib.decrypt(buffer, key.getAsLongArray());
            }
        }, Long.SIZE*2/Byte.SIZE);
    }

    @Override
    public InputStream filterOutgoingMessage(InputStream inputStream) {
        return new FilteredSocketInputStream(inputStream, new FilteredSocketInputStream.FilterBufferAction() {
            @Override
            public void bufferAction(byte[] buffer) {
                nativeLib.encrypt(buffer, key.getAsLongArray());
            }
        }, Long.SIZE*2/Byte.SIZE);
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
