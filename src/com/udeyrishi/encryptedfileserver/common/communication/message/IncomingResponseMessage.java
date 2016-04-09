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

package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.utils.ByteUtils;
import com.udeyrishi.encryptedfileserver.common.utils.Pair;
import com.udeyrishi.encryptedfileserver.common.utils.StreamCopier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class IncomingResponseMessage extends IncomingMessage {
    private boolean streamReadUntilAttachment = false;
    private String type = null;
    private String content = null;
    private long attachmentSize = -1;

    public IncomingResponseMessage(InputStream stream) {
        super(stream);
    }

    @Override
    public String getType() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return type;
    }

    @Override
    public String getContent() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return content;
    }

    @Override
    public InputStream getAttachmentStream() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return stream;
    }

    @Override
    public long getAttachmentSize() throws IOException, BadMessageException {
        readMessageUntilAttachment();
        return attachmentSize;
    }

    private void readMessageUntilAttachment() throws IOException, BadMessageException {
        if (streamReadUntilAttachment) {
            return;
        }

        InputStream filteredStream = stream;
        if (getFilter() != null) {
            filteredStream = getFilter().filterIncomingMessage(stream);
        }
        this.stream = filteredStream;

        byte[] messageBytes = readStreamUntilNewlineOrEOF();
        Pair<String, String> typeAndContent = parseMessage(new String(messageBytes));
        if (messageBytes[messageBytes.length - 1] == (byte) '\n') {
            attachmentSize = readAttachmentSize();
        }

        this.type = typeAndContent.getFirst();
        this.content = typeAndContent.getSecond();
        streamReadUntilAttachment = true;
    }

    private long readAttachmentSize() {
        final byte[] attachmentSizeBytes = new byte[Long.SIZE / Byte.SIZE];
        new StreamCopier(new OutputStream() {
            int i = 0;

            @Override
            public void write(int b) throws IOException {
                attachmentSizeBytes[i++] = (byte) b;
            }
        }, stream, Long.SIZE / Byte.SIZE).run();
        return ByteUtils.bytesToLong(attachmentSizeBytes);
    }

    private byte[] readStreamUntilNewlineOrEOF() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (true) {
            int lastByte = stream.read();

            if (lastByte == -1) {
                break;
            } else if ((byte) (lastByte & 0xFF) == (byte) '\n') {
                byteArrayOutputStream.write((byte) (lastByte & 0xFF));
                break;
            } else {
                byteArrayOutputStream.write((byte) (lastByte & 0xFF));
            }
        }

        return byteArrayOutputStream.toByteArray();
    }
}
