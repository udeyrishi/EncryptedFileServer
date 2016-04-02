package com.udeyrishi.encryptedfileserver.server.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.communication.message.IncomingMessage;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rishi on 2016-04-02.
 */
public class IncomingRequestMessage extends IncomingMessage {
    private static final String REGEX_PATTERN = "\\s*type\\s*:\\s*(.+?)\\s*;\\s*content\\s*:(.+)";

    private boolean streamRead = false;
    private String type = null;
    private String content = null;

    public IncomingRequestMessage(InputStream stream) {
        super(stream);
    }

    @Override
    public String getType() throws IOException, BadMessageException {
        readMessage();
        return type;
    }

    @Override
    public String getContent() throws IOException, BadMessageException {
        readMessage();
        return content;
    }

    private void readMessage() throws IOException, BadMessageException {
        if (streamRead) {
            return;
        }

        parseMessage(new String(readFromStream()));
        streamRead = true;
    }

    private void parseMessage(String message) throws BadMessageException {
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
    }

}
