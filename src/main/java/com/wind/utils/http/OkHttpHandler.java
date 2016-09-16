package com.wind.utils.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class OkHttpHandler implements INetworkHandler {
    private OkHttpClient client;

    public OkHttpHandler() {
        this.client = OkHttpClientInstance.getInstance();
    }

    @Override
    public void asyncRequest(final Request request, final ICallback callback) {
        com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder().url(request.getUrl());
        if(request.getTag() != null){
            builder.tag(request.getTag());
        }

        com.squareup.okhttp.Request okHttpRequest = builder.build();
        client.newCall(okHttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request r, IOException e) {
                callback.onFailure(request, e);
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                ResponseBody responseBody = response.body();
                Response result = new Response(response.code(), responseBody.contentLength(), responseBody.byteStream());
                callback.onResponse(request, result);
            }
        });
    }

    @Override
    public Response synRequest(Request request) {
        try{
            com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder().url(request.getUrl());
            if(request.getTag() != null){
                builder.tag(request.getTag());
            }

            //add headers
            Map<String, String> headers = request.getHeaders();
            if (headers != null) {
                Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                for (Iterator<Map.Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> it = iterator.next();
                    builder.header(it.getKey(), it.getValue());
                }
            }

            com.squareup.okhttp.Request okHttpRequest = builder.build();
            com.squareup.okhttp.Response response = client.newCall(okHttpRequest).execute();
            boolean fromCache = response.cacheResponse() != null;

            ResponseBody responseBody = response.body();
            Response result = new Response(response.code(), responseBody.contentLength(), new OkHttpInputStream(responseBody.byteStream(), responseBody));
            result.setCached(fromCache);
            return result;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void cancel(Object tag) {
        client.cancel(tag);
    }
}
