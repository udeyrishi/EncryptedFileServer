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
