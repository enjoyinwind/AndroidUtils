package com.wind.utils.http;

import android.content.Context;
import android.os.StatFs;

import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuxiaofeng02 on 2016/9/16.
 */
public class OkHttpClientInstance {
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static OkHttpClient instance;

    public static OkHttpClient getInstance(){
        if(instance == null){
            instance = defaultOkHttpClient();
        }

        return instance;
    }

    public static OkHttpClient getInstance(Context context){
        if(instance == null){
            instance = defaultOkHttpClient();
            File cacheDir = createDefaultCacheDir(context);
            instance.setCache(new com.squareup.okhttp.Cache(cacheDir, calculateDiskCacheSize(cacheDir)));
        }

        return instance;
    }

    public static void setInstance(OkHttpClient client){
        if(instance != null){
            throw new IllegalStateException("instance has already been initialized.");
        }

        instance = client;
    }

    private static OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return client;
    }

    private static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "okHttpCache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }
}
