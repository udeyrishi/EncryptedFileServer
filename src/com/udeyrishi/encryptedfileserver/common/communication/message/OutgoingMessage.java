package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.communication.message.filters.OutgoingMessageFilter;
import com.udeyrishi.encryptedfileserver.common.tea.TEAFileServerProtocolStandard;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public abstract class OutgoingMessage {
    private static final String FORMATTER_PATTERN = "type:%s;content:%s\n";

    private final String type;
    private final String content;
    private OutgoingMessageFilter filter = null;

    public OutgoingMessage(String type, String content) {
        this.type = Preconditions.checkNotNull(type, "type");
        if (content == null) {
            this.content = TEAFileServerProtocolStandard.SpecialContent.NULL_CONTENT;
        } else {
            this.content = content;
        }
    }

    protected static String serializedMessage(String type, String content) {
        return String.format(FORMATTER_PATTERN, type, content);
    }

    protected String getType() {
        return type;
    }

    protected String getContent() {
        return content;
    }

    public final InputStream getStream() {
        if (filter == null) {
            return getRawStream();
        } else {
            return filter.filterOutgoingMessage(getRawStream());
        }
    }

    protected abstract InputStream getRawStream();

    public void setFilter(OutgoingMessageFilter filter) {
        this.filter = filter;
    }

    public OutgoingMessageFilter getFilter() {
        return this.filter;
    }
}
