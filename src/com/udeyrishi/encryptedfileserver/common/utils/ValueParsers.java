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
