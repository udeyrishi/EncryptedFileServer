/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

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
