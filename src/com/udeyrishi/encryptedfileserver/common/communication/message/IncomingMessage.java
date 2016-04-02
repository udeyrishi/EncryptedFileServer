package com.udeyrishi.encryptedfileserver.common.communication.message;

import com.udeyrishi.encryptedfileserver.common.communication.BadMessageException;
import com.udeyrishi.encryptedfileserver.common.utils.Preconditions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;

/**
 * Created by rishi on 2016-04-02.
 */
public abstract class IncomingMessage {
    private final InputStream stream;

    public IncomingMessage(InputStream stream) {
        this.stream = Preconditions.checkNotNull(stream, "stream");
    }

    public abstract String getType() throws IOException, BadMessageException;
    public abstract String getContent() throws IOException, BadMessageException;

    protected byte[] readFromStream() throws IOException {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int n = stream.read(buffer);
            if (n < 0) {
                break;
            }
            packet.write(buffer, 0, n);
        }

        if (packet.size() == 0) {
            throw new StreamCorruptedException("Expected data on the stream, but nothing received.");
        }
        return packet.toByteArray();

    }

}