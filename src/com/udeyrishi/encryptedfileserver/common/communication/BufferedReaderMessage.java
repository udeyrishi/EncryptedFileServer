package com.udeyrishi.encryptedfileserver.common.communication;

import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by rishi on 2016-03-30.
 */
public class BufferedReaderMessage implements Message {
    private final BufferedReader reader;

    public BufferedReaderMessage(BufferedReader reader) {
        this.reader = Preconditions.checkNotNull(reader, "reader");
    }

    @Override
    public String getStringMessage() throws IOException {
        return reader.readLine();
    }
}
