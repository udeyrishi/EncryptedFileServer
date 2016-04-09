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

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by rishi on 2016-03-28.
 */
public class BigIntegerParser {
    private static final int DEFAULT_RADIX = 10;

    private static final HashMap<String, Integer> SUPPORTED_RADICES = new HashMap<String, Integer>() {{
        put("0x", 16);
        put("0b", 2);
        put("0o", 8);
    }};

    public static BigInteger parseBigInteger(String s) {
        s = s.trim();
        if (SUPPORTED_RADICES.containsKey(s.substring(0, 2))) {
            return new BigInteger(s.substring(2).replaceAll("\\s+", ""), SUPPORTED_RADICES.get(s.substring(0, 2)));
        } else {
            return new BigInteger(s.replaceAll("\\s+", ""), DEFAULT_RADIX);
        }
    }
}
