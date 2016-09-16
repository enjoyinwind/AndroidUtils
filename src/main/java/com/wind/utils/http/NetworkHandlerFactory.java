package com.wind.utils.http;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class NetworkHandlerFactory {
    public static INetworkHandler createHandler(){
        return new OkHttpHandler();
    }
}
