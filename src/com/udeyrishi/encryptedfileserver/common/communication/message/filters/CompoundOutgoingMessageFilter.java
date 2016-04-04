package com.udeyrishi.encryptedfileserver.common.communication.message.filters;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rishi on 2016-04-03.
 */
public class CompoundOutgoingMessageFilter implements OutgoingMessageFilter {

    private final List<OutgoingMessageFilter> filters;

    public CompoundOutgoingMessageFilter(OutgoingMessageFilter... filters) {
        this.filters = Arrays.asList(filters);
    }

    @Override
    public InputStream filterOutgoingMessage(InputStream messageStream) {
        for (OutgoingMessageFilter filter : filters) {
            messageStream = filter.filterOutgoingMessage(messageStream);
        }
        return messageStream;
    }

    public void addFilter(OutgoingMessageFilter filter) {
        filters.add(filter);
    }

    public void removeFilter(OutgoingMessageFilter filter) {
        filters.remove(filter);
    }
}
