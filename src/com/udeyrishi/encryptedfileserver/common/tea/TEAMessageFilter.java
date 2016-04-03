package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.message.FilteredSocketInputStream;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.OutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.InputStream;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAMessageFilter implements IncomingMessageFilter, OutgoingMessageFilter {

    private final TEAKey key;
    private final TEANative nativeLib;

    public TEAMessageFilter(TEAKey key) {
        this.key = Preconditions.checkNotNull(key, "key");
        this.nativeLib = new TEANative();
    }

    @Override
    public InputStream filterIncomingMessage(final InputStream inputStream) {
        return new FilteredSocketInputStream(inputStream, new FilteredSocketInputStream.FilterBufferAction() {
            @Override
            public void bufferAction(byte[] buffer) {
                System.out.println("Calling decrypt");
                nativeLib.decrypt(buffer, key.getAsLongArray());
                System.out.println("Called decrypt");
            }
        }, Long.SIZE*2/Byte.SIZE);
    }

    @Override
    public InputStream filterOutgoingMessage(InputStream inputStream) {
        return new FilteredSocketInputStream(inputStream, new FilteredSocketInputStream.FilterBufferAction() {
            @Override
            public void bufferAction(byte[] buffer) {
                System.out.println("Calling encrypt");
                nativeLib.encrypt(buffer, key.getAsLongArray());
                System.out.println("Called encrypt");
            }
        }, Long.SIZE*2/Byte.SIZE);

    }
}
