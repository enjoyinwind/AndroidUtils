package com.wind.utils.executor;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class UIHandler {
    private static Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable){
        if(null == runnable){
            throw new NullPointerException("Param 'runnable' can not be null.");
        }

        if(isMain()){
            runnable.run();
        } else {
            mMainHandler.post(runnable);
        }
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
