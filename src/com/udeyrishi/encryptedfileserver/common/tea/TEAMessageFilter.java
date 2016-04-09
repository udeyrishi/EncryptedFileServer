/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package com.udeyrishi.encryptedfileserver.common.tea;

import com.udeyrishi.encryptedfileserver.common.communication.message.FilteredSocketInputStream;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.OutgoingMessageFilter;
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
        }, Long.SIZE * 2 / Byte.SIZE);
    }

    @Override
    public InputStream filterOutgoingMessage(InputStream inputStream) {
        return new FilteredSocketInputStream(inputStream, new FilteredSocketInputStream.FilterBufferAction() {
            @Override
            public void bufferAction(byte[] buffer) {
                nativeLib.encrypt(buffer, key.getAsLongArray());
            }
        }, Long.SIZE * 2 / Byte.SIZE);
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
