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
 * Created by rishi on 2016-04-04.
 */
public class ValueParsers {
    public static ValueParser<Integer> createIntegerParser(final String argumentDescription) {
        return new ValueParser<Integer>() {
            @Override
            public String getDescription() {
                return argumentDescription;
            }

            @Override
            public String getParsedTypeName() {
                return "Integer";
            }

            @Override
            public Integer parse(String argValue) throws IllegalArgumentException {
                try {
                    return Integer.parseInt(argValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format("The %s needs to be an integer.",
                            getDescription().toLowerCase()), e);
                }

            }
        };
    }

    public static ValueParser<String> createStringParser(final String argumentDescription) {
        return new ValueParser<String>() {
            @Override
            public String getDescription() {
                return argumentDescription;
            }

            @Override
            public String getParsedTypeName() {
                return "String";
            }

            @Override
            public String parse(String argValue) throws IllegalArgumentException {
                return argValue;
            }
        };
    }
}
