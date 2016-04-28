package com.cabalry.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * CabalryApp
 */
public class CabalryApp extends Application {
    private static final String TAG = "CabalryApp";

    private static boolean activityVisible;
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    private static int created;
    private static int destroyed;

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public static boolean isApplicationRunning() {
        return created > destroyed;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }

    public class LifecycleHandler implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ++created;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ++destroyed;

            if (!isApplicationRunning()) {
                Log.i(TAG, "SENT: com.cabalry.action.APP_CLOSED");

                Intent intent = new Intent();
                intent.setAction("com.cabalry.action.APP_CLOSED");
                sendBroadcast(intent);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ++resumed;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            ++paused;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++started;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            ++stopped;
        }
    }
}
