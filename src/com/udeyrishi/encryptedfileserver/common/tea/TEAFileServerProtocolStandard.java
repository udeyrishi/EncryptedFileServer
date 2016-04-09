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

package com.udeyrishi.encryptedfileserver.common.tea;

/**
 * Created by rishi on 2016-03-30.
 */
public class TEAFileServerProtocolStandard {

    public static final int ENCRYPTION_KEY_BIT_COUNT = 256;

    public static class SpecialContent {
        public static final String NULL_CONTENT = "No-Content";
        public static final String NULL_ESCAPE = "\\";

        public static final String ACCESS_DENIED = "Access-Denied";
        public static final String ACCESS_GRANTED = "Access-Granted";

        public static final String BAD_FILE_REQUEST = "Bad-Request";
        public static final String FILE_NOT_FOUND = "File-Not-Found";
    }

    public static class TypeNames {
        public static final String AUTH_REQUEST = "Auth-Request";
        public static final String AUTH_RESPONSE = "Auth-Response";

        public static final String FILE_REQUEST = "File-Request";
        public static final String FILE_RESPONSE_FAILURE = "File-Response-Failure";
        public static final String FILE_RESPONSE_SUCCESS = "File-Response-Success";

        public static final String TERMINATION_REQUEST = "Termination-Request";

        public static final String INTERRUPT_NOTIFICATION = "Interrupt";
    }
//
//    public static class StandardMessages {
//
//        public static Message authenticationRequest(String userID) {
//            return MessageBuilder.requestMessage().addType(TypeNames.AUTH_REQUEST).addContent(userID).build();
//        }
//
//        public static Message fileRequest(String fileName) {
//            return MessageBuilder.requestMessage().addType(TypeNames.FILE_REQUEST).addContent(fileName).build();
//        }
//    }
}
