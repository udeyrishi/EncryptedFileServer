package com.udeyrishi.encryptedfileserver.common.communication.message.filters;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rishi on 2016-04-03.
 */
public class CompoundIncomingMessageFilter implements IncomingMessageFilter {

    private final List<IncomingMessageFilter> filters;

    public CompoundIncomingMessageFilter(IncomingMessageFilter... filters) {
        this.filters = Arrays.asList(filters);
    }

    @Override
    public InputStream filterIncomingMessage(InputStream messageStream) {
        for (IncomingMessageFilter filter : filters) {
            messageStream = filter.filterIncomingMessage(messageStream);
        }
        return messageStream;
    }

    @Override
    public void turnOnRawMessageCaching() {
        throw new UnsupportedOperationException("Internal filters might support caching");
    }

    @Override
    public void turnOffRawMessageCaching() {
        throw new UnsupportedOperationException("Internal filters might support caching");
    }

    @Override
    public InputStream getRawMessageCache() {
        throw new UnsupportedOperationException("Internal filters might support caching");
    }

    @Override
    public boolean isRawMessageCachingSupported() {
        return false;
    }

    public void addFilter(IncomingMessageFilter filter) {
        filters.add(filter);
    }

    public void removeFilter(IncomingMessageFilter filter) {
        filters.remove(filter);
    }
}