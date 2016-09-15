package com.wind.utils.executor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.os.Process;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class DefaultExecutorService extends ThreadPoolExecutor{
    private static final int DEFAULT_THREAD_COUNT = 3;
    private static DefaultExecutorService instance;
    private NetworkBroadcastReceiver mReceiver;
    private Context mContext;

    private DefaultExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(), new BackgroundThreadFactory());

    }

    private DefaultExecutorService(final Context context){
        this();

        mContext = context.getApplicationContext();
        mReceiver = new NetworkBroadcastReceiver(this);
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                mReceiver.register(mContext);
            }
        });
    }

    public static DefaultExecutorService getInstance(){
        if(instance == null){
            instance = new DefaultExecutorService();
        }

        return instance;
    }

    public static DefaultExecutorService getInstance(Context context){
        if(instance == null){
            instance = new DefaultExecutorService(context);
        }

        return instance;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                mReceiver.unregister(mContext);
            }
        });
    }

    void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(DEFAULT_THREAD_COUNT);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(DEFAULT_THREAD_COUNT);
                }
                break;
            default:
                setThreadCount(DEFAULT_THREAD_COUNT);
        }
    }

    void setThreadCount(int threadCount) {
        if (Build.VERSION.SDK_INT > 23) {
            setMaximumPoolSize(threadCount);
            setCorePoolSize(threadCount);
        } else {
            setCorePoolSize(threadCount);
            setMaximumPoolSize(threadCount);
        }
    }

    private static class BackgroundThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r) {
            return new BackgroundThread(r);
        }
    }

    private static class BackgroundThread extends Thread {
        public BackgroundThread(Runnable r) {
            super(r);
        }

        @Override
        public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
