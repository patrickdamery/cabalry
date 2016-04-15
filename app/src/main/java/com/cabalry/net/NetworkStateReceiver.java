package com.cabalry.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cabalry.app.CabalryApp;
import com.cabalry.app.ForgotActivity;
import com.cabalry.app.HomeActivity;
import com.cabalry.app.LoginActivity;
import com.cabalry.app.RegisterActivity;
import com.cabalry.util.TasksUtil;

/**
 * NetworkStateReceiver
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";

    TasksUtil.CheckNetworkTask checkNetworkTask;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "Network changed");

        if (checkNetworkTask == null) {
            if (CabalryApp.isActivityVisible() || CabalryApp.isApplicationInForeground() || CabalryApp.isApplicationVisible()) {
                if (!LoginActivity.active && !HomeActivity.active) {
                    Log.d(TAG, "performing network check");
                    performNetworkCheck(context);
                }
            }
        }
    }

    private void performNetworkCheck(final Context context) {
        checkNetworkTask = new TasksUtil.CheckNetworkTask(context) {

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result && !HomeActivity.active) { // no internet, redirect to home
                    Intent intent = new Intent(context, HomeActivity.class);
                    if (ForgotActivity.active || RegisterActivity.active) {
                        intent = new Intent(context, LoginActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                checkNetworkTask = null;
            }
        };

        checkNetworkTask.execute();
    }
}
