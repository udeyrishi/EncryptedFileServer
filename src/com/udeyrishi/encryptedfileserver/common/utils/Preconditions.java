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

package com.udeyrishi.encryptedfileserver.common.utils;

/**
 * Created by rishi on 2016-03-28.
 */
public class Preconditions {
    public static <T> T checkNotNull(T obj, String argName) {
        if (obj == null) {
            throw new NullPointerException(String.format("Argument: '%s' can't be null.", argName));
        }

        return obj;
    }
}
