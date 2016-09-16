package com.wind.utils.http;

import java.util.Map;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class Request {
    private Object tag;
    private String url;
    private String method;
    private Map<String, String> headers;
    private byte[] data;

    public Request(Object tag, String url, Map<String, String> headers) {
        this.tag = tag;
        this.url = url;
        this.headers = headers;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
