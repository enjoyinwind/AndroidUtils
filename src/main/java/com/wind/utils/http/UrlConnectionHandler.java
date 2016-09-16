package com.wind.utils.http;

import android.net.Uri;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class UrlConnectionHandler implements INetworkHandler{
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s

    @Override
    public void asyncRequest(Request request, ICallback callback) {
        throw new UnsupportedOperationException("asyncRequest not supported.");
    }

    @Override
    public Response synRequest(Request request) {
        HttpURLConnection connection = null;
        try {
            Uri uri = Uri.parse(request.getUrl());
            connection = openConnection(uri);
            connection.setUseCaches(true);

            //add headers
            Map<String, String> headers = request.getHeaders();
            if(null != headers){
                Set<Map.Entry<String, String>> set = headers.entrySet();
                for(Map.Entry<String, String> entry : set){
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            long contentLength = connection.getHeaderFieldInt("Content-Length", -1);
            return new Response(connection.getResponseCode(), contentLength, new UrlConnectionInputStream(connection.getInputStream(), connection));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void cancel(Object tag) {
        //do nothing
    }

    protected HttpURLConnection openConnection(Uri path) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(path.toString()).openConnection();
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS);
        return connection;
    }
}
