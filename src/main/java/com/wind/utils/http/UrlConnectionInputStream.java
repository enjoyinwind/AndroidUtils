package com.wind.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by liuxiaofeng02 on 2016/9/16.
 */
public class UrlConnectionInputStream extends WrapperInputStream{
    private HttpURLConnection connection;

    public UrlConnectionInputStream(InputStream inputStream, HttpURLConnection connection) {
        super(inputStream);
        this.connection = connection;
    }

    @Override
    public void close() throws IOException {
        super.close();
        connection.disconnect();
    }
}
