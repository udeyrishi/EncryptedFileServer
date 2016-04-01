package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.InputStream;

/**
 * Created by rishi on 2016-03-31.
 */
public class MessageBuilder {
    private String type = null;
    private String content = null;
    private BufferedReader contentReader = null;
    private BufferedReader entireMessageReader = null;
    private boolean autoCloseStream = false;
    private InputStream attachmentStream = null;

    private MessageBuilder() {
    }

    public static MessageBuilder requestMessage() {
        return new MessageBuilder();
    }

    public static MessageBuilder responseMessage() {
        return new MessageBuilder();
    }

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

    public MessageBuilder addAttachmentStream(InputStream attachmentStream) {
        this.attachmentStream = attachmentStream;
        return this;
    }

    private void nullifyContent() {
        entireMessageReader = null;
        contentReader = null;
        content = null;
    }

    public Message build() {
        Message message;

        if (entireMessageReader != null) {
            message = new BufferedReaderMessage(entireMessageReader, autoCloseStream);
        } else if (contentReader != null) {
            message = new BufferedReaderMessage(type, contentReader, autoCloseStream);
        } else {
            message = new Message(type, content);
        }

        message.addAttachmentStream(attachmentStream);
        return message;
    }
}
