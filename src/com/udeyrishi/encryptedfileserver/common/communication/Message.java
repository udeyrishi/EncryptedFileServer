package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public class Message {
    private static final String FORMATTER_PATTERN = "type:%s;content:%s";

    protected String messageContent = null;
    protected String typeName = null;
    private byte[] attachment = null;

    Message(String typeName, String messageContent) {
        this.typeName = Preconditions.checkNotNull(typeName, "typeName");
        this.messageContent = messageContent;
    }

    protected Message() {
    }

    private static boolean nullSafeComparison(Object first, Object second) {
        return first == second || !(first == null || second == null) && first.equals(second);
    }

    public String getTypeName() throws IOException, BadMessageException {
        return typeName;
    }

    public String getMessageContents() throws IOException, BadMessageException {
        return messageContent;
    }

    // Needed because equals() can't throw exception
    public boolean isEqualTo(Message second) throws IOException, BadMessageException {
        if (this == second) {
            return true;
        }

        if (second == null) {
            return false;
        }

        return this.getTypeName().equals(second.getTypeName()) &&
                nullSafeComparison(this.getMessageContents(), second.getMessageContents());

    }

    public String serializeMessage() throws IOException, BadMessageException {
        String type = getTypeName();
        String contents = getMessageContents();

        if (contents == null) {
            contents = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
        } else if (contents.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
            // add escape
            contents = "\\" + TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
        }

        return String.format(FORMATTER_PATTERN, type, contents);
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void addAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
