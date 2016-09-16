package com.wind.utils.http;

import java.io.InputStream;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class Response {
    //http响应吗
    private int code;
    private InputStream stream;
    private long contentLength;
    private boolean cached;

    public Response(int code, long contentLength, InputStream stream) {
        this.code = code;
        this.contentLength = contentLength;
        this.stream = stream;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
