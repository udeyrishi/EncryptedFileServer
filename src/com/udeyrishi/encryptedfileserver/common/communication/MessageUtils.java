package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rishi on 2016-03-30.
 */
public class MessageUtils {
    private static final String REGEX_PATTERN = "\\s*type\\s*:\\s*(.+?)\\s*;\\s*content\\s*:(.+)";
    private static final String FORMATTER_PATTERN = "type:%s;content:%s";

    public static Message parseMessage(String message) throws BadMessageException {
        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(message);
        if (matcher.find()) {
            String type = matcher.group(1);
            String content = matcher.group(2);
            if (content.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                content = null;
            } else if (content.equals("\\" + TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
                // remove escape
                content = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
            }
            return new StringMessage(type, content);
        } else {
            throw new BadMessageException(message);
        }
    }

    public static String serializeMessage(Message message) throws IOException, BadMessageException {
        String type = message.getTypeName();
        String contents = message.getMessageContents();

        if (contents == null) {
            contents = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
        } else if (contents.equals(TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT)) {
            // add escape
            contents = "\\" + TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
        }

        return String.format(FORMATTER_PATTERN, type, contents);
    }

    // Needed because equals() can't throw exception
    public static boolean areEqual(Message first, Message second) throws IOException, BadMessageException {
        if (first == second) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        if (first.getTypeName().equals(second.getTypeName()) &&
                nullSafeComparison(first.getMessageContents(), second.getMessageContents())) {
            return true;
        }

        return false;
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
