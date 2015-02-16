package com.cabalry.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class SilentAlarmReceiver extends WakefulBroadcastReceiver {

    private static int count = 0;
    private static Timer timer;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(!Preferences.getBoolean(GlobalKeys.SILENT)) return;
        count++;

        if (count > 5) {
            count = 0;
            startSilentAlarm(context);
        }

        if(timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                count = 0;
                timer = null;
            }
        }, 5000);
    }

    private void startSilentAlarm(Context context) {

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, new Intent(context, SilentAlarmService.class));
    }
}