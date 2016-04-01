package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;

/**
 * Created by rishi on 2016-03-31.
 */
public class MessageBuilder {
    private String type = null;
    private String content = null;
    private BufferedReader contentReader = null;
    private BufferedReader entireMessageReader = null;
    private boolean autoCloseStream = false;
    private boolean readUntilNewLine = true;

    public MessageBuilder addType(String type) {
        this.type = Preconditions.checkNotNull(type, "type");
        entireMessageReader = null;
        return this;
    }

    public MessageBuilder addContent(String content) {
        nullifyContent();
        this.content = content;
        return this;
    }

    public MessageBuilder addContent(BufferedReader contentReader) {
        nullifyContent();
        this.contentReader = Preconditions.checkNotNull(contentReader, "contentReader");
        return this;
    }

    public MessageBuilder addTypeAndContent(BufferedReader entireMessageReader) {
        nullifyContent();
        type = null;
        this.entireMessageReader = Preconditions.checkNotNull(entireMessageReader, "entireMessageReader");
        return this;
    }

    public MessageBuilder autoCloseStream(boolean autoCloseStream) {
        this.autoCloseStream = autoCloseStream;
        return this;
    }

    public static MessageBuilder requestMessage() {
        MessageBuilder builder = new MessageBuilder();
        builder.readUntilNewLine = true;
        return builder;
    }

    public static MessageBuilder responseMessage() {
        MessageBuilder builder = new MessageBuilder();
        builder.readUntilNewLine = false;
        return builder;
    }

    private void nullifyContent() {
        entireMessageReader = null;
        contentReader = null;
        content = null;
    }

    private MessageBuilder() {    }


    public Message build() {
        if (entireMessageReader != null) {
            return new BufferedReaderMessage(entireMessageReader, autoCloseStream, readUntilNewLine);
        }

        if (contentReader != null) {
            return new BufferedReaderMessage(type, contentReader, autoCloseStream, readUntilNewLine);
        }

        return new StringMessage(type, content);
    }
}
