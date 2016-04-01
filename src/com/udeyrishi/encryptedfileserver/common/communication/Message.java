package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;

import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public abstract class Message {
    private static final String FORMATTER_PATTERN = "type:%s;content:%s";

    public abstract String getTypeName() throws IOException, BadMessageException;
    public abstract String getMessageContents() throws IOException, BadMessageException;

    // Needed because equals() can't throw exception
    public boolean isEqualTo(Message second) throws IOException, BadMessageException {
        if (this == second) {
            return true;
        }

        if (second == null) {
            return false;
        }

        if (this.getTypeName().equals(second.getTypeName()) &&
                nullSafeComparison(this.getMessageContents(), second.getMessageContents())) {
            return true;
        }

        return false;
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

    private static boolean nullSafeComparison(Object first, Object second) {
        if (first == second) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        return first.equals(second);
    }
}
