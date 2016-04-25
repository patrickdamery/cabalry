package com.cabalry.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * CabalryApp
 */
public class CabalryApp extends Application {

    private static boolean activityVisible;
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

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

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }

    public class LifecycleHandler implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
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
