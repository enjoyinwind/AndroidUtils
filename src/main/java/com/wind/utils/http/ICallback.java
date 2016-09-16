package com.wind.utils.http;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public interface ICallback {
    void onResponse(Request request, Response response);
    void onFailure(Request request, Exception e);
}
