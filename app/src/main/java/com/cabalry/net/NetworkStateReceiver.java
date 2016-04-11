package com.cabalry.net;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cabalry.app.HomeActivity;
import com.cabalry.util.TasksUtil;

import java.util.List;

/**
 * NetworkStateReceiver
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.i(TAG, "Network changed");

        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++) {

            if(procInfos.get(i).processName.equals("com.cabalry")) {
                performNetworkCheck(context);
            }
        }
    }

    private void performNetworkCheck(final Context context) {
        new TasksUtil.CheckNetworkTask(context) {

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result) {
                    // redirect to home, update preferences to no network (not sure yet)
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }.execute();
    }
}
