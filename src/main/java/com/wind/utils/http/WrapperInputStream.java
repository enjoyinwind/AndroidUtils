package com.wind.utils.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liuxiaofeng02 on 2016/9/16.
 */
public class WrapperInputStream extends InputStream {
    private InputStream inputStream;

    public WrapperInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        return inputStream.skip(byteCount);
    }
}
