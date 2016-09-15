package com.wind.utils.executor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by liuxiaofeng02 on 2016/9/15.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static final String EXTRA_AIRPLANE_STATE = "state";
    private DefaultExecutorService executorService;

    public NetworkBroadcastReceiver(DefaultExecutorService defaultExecutorService) {
        this.executorService = defaultExecutorService;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // On some versions of Android this may be called with a null Intent,
        // also without extras (getExtras() == null), in such case we use defaults.
        if (intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
                return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
            }

            if(intent.getBooleanExtra(EXTRA_AIRPLANE_STATE, false)){
                executorService.setThreadCount(1);
            }
        } else if (CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            executorService.adjustThreadCount(connectivityManager.getActiveNetworkInfo());
        }
    }
}
