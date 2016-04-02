package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
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

    protected final InputStream stream;

    public IncomingMessage(InputStream stream) {
        this.stream = Preconditions.checkNotNull(stream, "stream");
    }

    public abstract String getType() throws IOException, BadMessageException;
    public abstract String getContent() throws IOException, BadMessageException;

    protected byte[] readFromStream() throws IOException {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int n = stream.read(buffer);
            if (n < 0) {
                break;
            }
            packet.write(buffer, 0, n);
        }

        if (packet.size() == 0) {
            throw new StreamCorruptedException("Expected data on the stream, but nothing received.");
        }
        return packet.toByteArray();

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

}
