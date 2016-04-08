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

    private final List<ValueParser<?>> positionalArgumentValueParsers = new ArrayList<>();
    private final List<String> positionalArgumentNames = new ArrayList<>();
    private final HashMap<String, ValueParser<?>> optionalArgumentParsers = new HashMap<>();

    private final List<String> parsedPositionalArgValues = new ArrayList<>();
    private final HashMap<String, String> parsedOptionalArgValues = new HashMap<>();

    private final HashMap<String, Object> parsingResults = new HashMap<>();

    public ArgumentParser(String[] args) {
        this.commandLineArgs = Preconditions.checkNotNull(args, "args");
    }

    protected String getUsage() {
        StringBuilder usage = new StringBuilder("usage: ");

        for (ValueParser<?> p : positionalArgumentValueParsers) {
            usage.append(String.format(" <%s>[%s]", p.getDescription(), p.getParsedTypeName()));
        }

        for (Map.Entry<String, ValueParser<?>> p : optionalArgumentParsers.entrySet()) {
            usage.append(String.format(" -%s[%s]", p.getKey(), p.getValue().getParsedTypeName()));
        }
        return usage.toString();
    }

    public void process() {
        parseArgs();
        try {
            for (int i = 0; i < parsedPositionalArgValues.size(); ++i) {
                Object parsedObject = positionalArgumentValueParsers.get(i).parse(parsedPositionalArgValues.get(i));
                parsingResults.put(positionalArgumentNames.get(i), parsedObject);
            }

            for (Map.Entry<String, String> optionalArg : parsedOptionalArgValues.entrySet()) {
                if (optionalArgumentParsers.containsKey(optionalArg.getKey())) {
                    ValueParser<?> valueParser = optionalArgumentParsers.get(optionalArg.getKey());
                    Object result = valueParser.parse(optionalArg.getValue());
                    parsingResults.put(optionalArg.getKey(), result);
                } else {
                    throw new IllegalArgumentException("");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + "\n" + getUsage(), e);
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

        if (this.parsedPositionalArgValues.size() != this.positionalArgumentValueParsers.size()) {
            throw new IllegalArgumentException(getUsage());
        }
    }

    public <T> void addPositionalArg(String argName, ValueParser<T> valueParser) {
        this.positionalArgumentValueParsers.add(valueParser);
        this.positionalArgumentNames.add(argName);
    }

    public <T> void addOptionalArg(String argName, ValueParser<T> valueParser, T defaultValue) {
        this.optionalArgumentParsers.put(argName, valueParser);
        this.parsingResults.put(argName, defaultValue);
    }

    public <T> void addOptionalArg(String argName, ValueParser<T> valueParser) {
        this.addOptionalArg(argName, valueParser, null);
    }
}
