package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;

/**
 * Created by rishi on 2016-03-31.
 */
public class MessageBuilder {
    private String type = null;
    private String content = null;
    private BufferedReader contentStream = null;
    private BufferedReader entireMessageStream = null;
    private boolean autoCloseStream = false;
    private boolean readUntilNewLine = true;

    public MessageBuilder addType(String type) {
        this.type = Preconditions.checkNotNull(type, "type");
        entireMessageStream = null;
        return this;
    }

    public MessageBuilder addContent(String content) {
        this.content = content;
        entireMessageStream = null;
        contentStream = null;
        return this;
    }

    public MessageBuilder addContent(BufferedReader contentStream) {
        this.contentStream = Preconditions.checkNotNull(contentStream, "contentStream");
        entireMessageStream = null;
        content = null;
        return this;
    }

    public MessageBuilder makeFromReader(BufferedReader entireMessageStream) {
        this.entireMessageStream = Preconditions.checkNotNull(entireMessageStream, "entireMessageStream");
        type = null;
        contentStream = null;
        content = null;
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

    private MessageBuilder() {    }


    public Message build() {
        if (entireMessageStream != null) {
            return new BufferedReaderMessage(entireMessageStream, autoCloseStream, readUntilNewLine);
        }

        if (contentStream != null) {
            return new BufferedReaderMessage(type, contentStream, autoCloseStream, readUntilNewLine);
        }

        return new StringMessage(type, content);
    }
}
