package com.udeyrishi.encryptedfileserver.common.communication;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rishi on 2016-03-30.
 */
public class MessageUtils {
    // Type can't be null, content can be null
    private static final String REGEX_PATTERN = "type:(.+);content:(.*)";
    private static final String FORMATTER_PATTERN = "type:%s;content:%s";

    public static Message parseMessage(String message) throws BadMessageException {
        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(message);
        if (matcher.find()) {
            String type = matcher.group(1);
            String content = matcher.group(2);
            return new StringMessage(type, content.isEmpty() ? null : content);
        } else {
            throw new BadMessageException(message);
        }
    }

    public static String serializeMessage(Message message) throws IOException, BadMessageException {
        String type = message.getTypeName();
        String contents = message.getMessageContents();
        return String.format(FORMATTER_PATTERN, type, contents == null ? "": contents);
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
