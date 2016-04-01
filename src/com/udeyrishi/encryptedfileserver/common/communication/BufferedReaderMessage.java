package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rishi on 2016-03-30.
 */
class BufferedReaderMessage extends Message {
    private static final String REGEX_PATTERN = "\\s*type\\s*:\\s*(.+?)\\s*;\\s*content\\s*:(.+)";

    private final BufferedReader reader;
    private final boolean autoCloseStream;

    // This flag is used to differentiate between no-content streamed messages vs. yet-uninitialised ones
    private boolean isRealMessageContentNull = false;

    BufferedReaderMessage(BufferedReader reader, boolean autoCloseReader) {
        this.reader = Preconditions.checkNotNull(reader, "reader");
        this.autoCloseStream = autoCloseReader;
    }

    BufferedReaderMessage(String typeName, BufferedReader contentReader, boolean autoCloseStream) {
        this(contentReader, autoCloseStream);
        this.typeName = Preconditions.checkNotNull(typeName, "typeName");
    }

    @Override
    public String getTypeName() throws IOException, BadMessageException {
        if (typeName == null) {
            readMessage();
        }
        return typeName;
    }

    @Override
    public String getMessageContents() throws IOException, BadMessageException {
        if (messageContent == null && !isRealMessageContentNull) {
            readMessage();
        }
        return messageContent;
    }

    private void readMessage() throws IOException, BadMessageException {
        if (typeName == null) {
            // Entire message is in the reader
            parseMessage(readFromReader());

            if (autoCloseStream) {
                reader.close();
            }
            if (this.messageContent == null) {
                isRealMessageContentNull = true;
            }
        } else {
            this.messageContent = readFromReader();
        }
    }

    private String readFromReader() throws IOException {
        String messageRead = reader.readLine();
        if (messageRead == null) {
            throw new SocketException("Null message received. Socket is suddenly terminated from client side.");
        }
        return messageRead;
    }

    private void parseMessage(String message) throws BadMessageException {
        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(message);
        if (matcher.find()) {
            typeName = matcher.group(1);
            messageContent = matcher.group(2);
            if (messageContent.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                messageContent = null;
            } else if (messageContent.equals("\\" + TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                // remove escape
                messageContent = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
            }
        } else {
            throw new BadMessageException(message);
        }
    }
}
