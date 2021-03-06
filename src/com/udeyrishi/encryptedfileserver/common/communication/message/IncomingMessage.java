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
import com.udeyrishi.encryptedfileserver.common.communication.message.filters.IncomingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Config;
import com.udeyrishi.encryptedfileserver.common.utils.Pair;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rishi on 2016-04-02.
 */
public abstract class IncomingMessage {
    private static final String REGEX_PATTERN = "\\s*type\\s*:\\s*(.+?)\\s*;\\s*content\\s*:(.+)\n";

    protected InputStream stream;

    private IncomingMessageFilter messageFilter = null;

    public IncomingMessage(InputStream stream) {
        this.stream = Preconditions.checkNotNull(stream, "stream");
    }

    protected static Pair<String, String> parseMessage(String message) throws BadMessageException {
        String type;
        String content;

        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(message);
        if (matcher.find()) {
            type = matcher.group(1);
            content = matcher.group(2);
            if (content.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                content = null;
            } else if (content.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_ESCAPE +
                    TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                // remove escape
                content = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
            }
        } else {
            throw new BadMessageException(message);
        }

        return new Pair<>(type, content);
    }

    public abstract String getType() throws IOException, BadMessageException;

    public abstract String getContent() throws IOException, BadMessageException;

    public abstract InputStream getAttachmentStream() throws IOException, BadMessageException;

    public abstract long getAttachmentSize() throws IOException, BadMessageException;

    public IncomingMessageFilter getFilter() {
        return this.messageFilter;
    }

    public void setFilter(IncomingMessageFilter filter) {
        this.messageFilter = filter;
    }

    protected byte[] readFromStream() throws IOException {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        InputStream filteredStream = stream;
        if (messageFilter != null) {
            filteredStream = messageFilter.filterIncomingMessage(stream);
        }

        byte[] buffer = new byte[Config.getConfig().getInt("BUFFER_SIZE_BYTES")];
        while (true) {
            int n = filteredStream.read(buffer);
            if (n < 0) {
                break;
            }
            packet.write(buffer, 0, n);
            if (buffer[n - 1] == (byte) '\n') {
                break;
            }
        }

        if (packet.size() == 0) {
            throw new StreamCorruptedException("Expected data on the stream, but nothing received.");
        }
        return packet.toByteArray();

    }
}
