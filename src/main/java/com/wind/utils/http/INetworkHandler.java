package com.wind.utils.http;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public interface INetworkHandler {
    Response synRequest(Request request);
    void asyncRequest(Request request, ICallback callback);
    void cancel(Object tag);
}
