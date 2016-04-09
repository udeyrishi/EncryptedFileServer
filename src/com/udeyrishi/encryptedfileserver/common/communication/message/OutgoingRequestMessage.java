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

package com.udeyrishi.encryptedfileserver.common.communication.message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rishi on 2016-04-02.
 */
public class OutgoingRequestMessage extends OutgoingMessage {

    public OutgoingRequestMessage(String type, String content) {
        super(type, content);
    }

    @Override
    protected InputStream getRawStream() {
        final byte[] requestMessage = serializedMessage(getType(), getContent()).getBytes();

        return new InputStream() {
            private int i = 0;

            @Override
            public int read() throws IOException {
                if (i < requestMessage.length) {
                    return requestMessage[i++];
                } else {
                    return -1;
                }
            }
        };
    }
}
