package com.udeyrishi.encryptedfileserver.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rishi on 2016-03-31.
 */
public class ArgumentParser {
    private final String[] commandLineArgs;

    private final List<Parser<?>> positionalArgumentParsers = new ArrayList<>();
    private final List<String> positionalArgumentNames = new ArrayList<>();
    private final HashMap<String, Parser<?>> optionalArgumentParsers = new HashMap<>();

    private final List<String> parsedPositionalArgValues = new ArrayList<>();
    private final HashMap<String, String> parsedOptionalArgValues = new HashMap<>();

    private final HashMap<String, Object> parsingResults = new HashMap<>();

    public ArgumentParser(String[] args) {
        this.commandLineArgs = Preconditions.checkNotNull(args, "args");
    }

    protected String getUsage() {
        StringBuilder usage = new StringBuilder("usage: ");

        for (Parser<?> p : positionalArgumentParsers) {
            usage.append(String.format(" <%s>[%s]", p.getDescription(), p.getParsedTypeName()));
        }

        for (Map.Entry<String, Parser<?>> p : optionalArgumentParsers.entrySet()) {
            usage.append(String.format(" -%s[%s]", p.getKey(), p.getValue().getParsedTypeName()));
        }
        return usage.toString();
    }

    public void process() {
        parseArgs();
        for (int i = 0; i < parsedPositionalArgValues.size(); ++i) {
            Object parsedObject = positionalArgumentParsers.get(i).parse(parsedPositionalArgValues.get(i));
            parsingResults.put(positionalArgumentNames.get(i), parsedObject);
        }

        for (Map.Entry<String, String> optionalArg : parsedOptionalArgValues.entrySet()) {
            if (optionalArgumentParsers.containsKey(optionalArg.getKey())) {
                Parser<?> parser = optionalArgumentParsers.get(optionalArg.getKey());
                Object result = parser.parse(optionalArg.getValue());
                parsingResults.put(optionalArg.getKey(), result);
            } else {
                throw new IllegalArgumentException(getUsage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String argName) {
        return (T) parsingResults.get(argName);
    }

    private void parseArgs() {
        for (int i = 0; i < commandLineArgs.length; ++i) {
            if (commandLineArgs[i].startsWith("-")) {
                String argKey = commandLineArgs[i].replaceFirst("-", "");
                if (i + 1 < commandLineArgs.length) {
                    this.parsedOptionalArgValues.put(argKey, commandLineArgs[++i]);
                } else {
                    throw new IllegalArgumentException(getUsage());
                }
            } else {
                this.parsedPositionalArgValues.add(commandLineArgs[i]);
            }
        }

        if (this.parsedPositionalArgValues.size() != this.positionalArgumentParsers.size()) {
            throw new IllegalArgumentException(getUsage());
        }
    }

    public <T> void addPositionalArg(String argName, int position, Parser<T> parser) {
        this.positionalArgumentParsers.add(position, parser);
        this.positionalArgumentNames.add(position, argName);
    }

    public <T> void addOptionalArg(String argName, Parser<T> parser, T defaultValue) {
        this.optionalArgumentParsers.put(argName, parser);
        this.parsingResults.put(argName, defaultValue);
    }

    public <T> void addOptionalArg(String argName, Parser<T> parser) {
        this.addOptionalArg(argName, parser, null);
    }

    public Parser<Integer> createIntegerParser(final String argumentDescription) {
        return new Parser<Integer>() {
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
                    throw new IllegalArgumentException(String.format("The %s needs to be an integer.\n%s",
                            getDescription().toLowerCase(), getUsage()), e);
                }

            }
        };
    }

    public Parser<String> createStringParser(final String argumentDescription) {
        return new Parser<String>() {
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

    public interface Parser<T> {
        String getDescription();

        String getParsedTypeName();

        T parse(String argValue) throws IllegalArgumentException;
    }
}
