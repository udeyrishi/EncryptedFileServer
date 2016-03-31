package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public class BufferedReaderMessage implements Message {
    private final BufferedReader reader;
    private final boolean autoCloseStream;

    private String messageContent = null;
    private String typeName = null;

    // This flag is used to differentiate between no-content streamed messages vs. yet-uninitialised ones
    private boolean isRealMessageContentNull = false;

    public BufferedReaderMessage(BufferedReader reader, boolean autoCloseReader) {
        this.reader = Preconditions.checkNotNull(reader, "reader");
        this.autoCloseStream = autoCloseReader;
    }

    public BufferedReaderMessage(String typeName, BufferedReader contentReader, boolean autoCloseStream) {
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
            Message message = MessageUtils.parseMessage(reader.readLine());
            if (autoCloseStream) {
                reader.close();
            }
            this.messageContent = message.getMessageContents();
            this.typeName = message.getTypeName();

            if (this.messageContent == null) {
                isRealMessageContentNull = true;
            }
        } else {
            this.messageContent = reader.readLine();
        }
    }
}
