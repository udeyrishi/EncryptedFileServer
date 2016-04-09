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

/**
 * Created by rishi on 2016-04-02.
 */
public interface IncomingMessageFilter {
    InputStream filterIncomingMessage(InputStream messageStream);

    void turnOnRawMessageCaching();

    void turnOffRawMessageCaching();

    InputStream getRawMessageCache();

    boolean isRawMessageCachingSupported();
}
