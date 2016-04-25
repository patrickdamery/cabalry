package com.cabalry.base;

import android.app.Service;
import android.util.Log;

/**
 * RunnableService
 */
public abstract class RunnableService extends Service {
    private static final String TAG = "RunnableService";

    private static boolean isRunning = false;

    public static boolean isRunning() {
        Log.d(TAG, "Running: " + isRunning);
        return isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
