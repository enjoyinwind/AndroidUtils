package com.wind.utils.http;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liuxiaofeng02 on 2016/9/16.
 */
public class OkHttpInputStream extends WrapperInputStream {
    private ResponseBody responseBody;

    public OkHttpInputStream(InputStream inputStream, ResponseBody responseBody) {
        super(inputStream);
        this.responseBody = responseBody;
    }

    @Override
    public void close() throws IOException {
        super.close();
        responseBody.close();
    }
}
